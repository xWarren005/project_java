package com.s2o.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s2o.app.dto.PaymentRequest;
import com.s2o.app.dto.response.CashierInvoiceDTO;
import com.s2o.app.dto.response.CashierOrderDetailDTO;
import com.s2o.app.dto.response.CashierTableDTO;
import com.s2o.app.entity.*;
import com.s2o.app.repository.InvoiceRepository;
import com.s2o.app.repository.OrderRepository;
import com.s2o.app.repository.TableRepository;
import com.s2o.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CashierService {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /* ===================================================
       PHẦN 1: DANH SÁCH BÀN
       =================================================== */
    @Transactional(readOnly = true)
    public List<CashierTableDTO> getTablesForCashier(Integer restaurantId) {

        List<RestaurantTable> tables =
                tableRepository.findByRestaurantId(restaurantId);

        List<CashierTableDTO> result = new ArrayList<>();

        for (RestaurantTable table : tables) {

            CashierTableDTO dto = new CashierTableDTO();
            dto.setId(table.getId());
            dto.setName(table.getTableName());
            dto.setCapacity(table.getCapacity());

            Optional<Order> activeOrderOpt =
                    orderRepository.findFirstByTableIdAndStatusInOrderByCreatedAtDesc(
                            table.getId(),
                            List.of(
                                    OrderStatus.PENDING,
                                    OrderStatus.COOKING,
                                    OrderStatus.READY,
                                    OrderStatus.PAYMENT_PENDING,
                                    OrderStatus.PAID
                            )
                    );

            if (activeOrderOpt.isPresent()) {
                Order order = activeOrderOpt.get();
                dto.setStatus("busy");

                if (order.getCreatedAt() != null) {
                    long minutes =
                            Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes();
                    dto.setTime(minutes + " phút trước");
                } else {
                    dto.setTime("Vừa xong");
                }

                dto.setTotal(
                        order.getTotalAmount() != null
                                ? order.getTotalAmount().doubleValue()
                                : 0.0
                );
            } else {
                dto.setStatus("empty");
                dto.setTime("");
                dto.setTotal(0.0);
            }

            dto.setOrders(new ArrayList<>());
            result.add(dto);
        }

        return result;
    }

    /* ===================================================
       PHẦN 2: DANH SÁCH HÓA ĐƠN
       =================================================== */
    @Transactional(readOnly = true)
    public List<CashierInvoiceDTO> getInvoicesForCashier() {

        Integer restaurantId = 1; // giữ nguyên như source gốc
        List<Order> orders =
                orderRepository.findOrdersByRestaurantId(restaurantId);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        List<CashierInvoiceDTO> result = new ArrayList<>();

        for (Order order : orders) {

            CashierInvoiceDTO dto = new CashierInvoiceDTO();
            dto.setId("INV-" + String.format("%03d", order.getId()));
            dto.setOrderId(Long.valueOf(order.getId()));

            if (order.getRestaurantTable() != null) {
                dto.setTable(order.getRestaurantTable().getTableName());
                dto.setTableId(order.getRestaurantTable().getId());
            } else {
                dto.setTable("Mang về");
                dto.setTableId(null);
            }

            dto.setTime(
                    order.getCreatedAt() != null
                            ? order.getCreatedAt().format(formatter)
                            : "N/A"
            );

            dto.setTotal(
                    order.getTotalAmount() != null
                            ? order.getTotalAmount()
                            : BigDecimal.ZERO
            );

            Optional<Invoice> invoiceOpt =
                    invoiceRepository.findByOrder_Id(order.getId());

            if (invoiceOpt.isPresent()) {
                dto.setStatus("PAID");

                Invoice inv = invoiceOpt.get();
                if (inv.getPaymentMethod() != null) {
                    String m = inv.getPaymentMethod().name();
                    if (m.contains("CASH")) dto.setMethod("Tiền mặt");
                    else if (m.contains("BANK")) dto.setMethod("Chuyển khoản");
                    else dto.setMethod("Ví điện tử");
                }
            } else {
                dto.setStatus("UNPAID");
                dto.setMethod(null);
            }

            List<CashierInvoiceDTO.ItemDTO> items =
                    order.getItems() != null
                            ? order.getItems().stream().map(item -> {
                        CashierInvoiceDTO.ItemDTO i =
                                new CashierInvoiceDTO.ItemDTO();
                        i.setName(
                                item.getProduct() != null
                                        ? item.getProduct().getName()
                                        : "Món đã xóa"
                        );
                        i.setQty(item.getQuantity());
                        i.setPrice(item.getUnitPrice());
                        return i;
                    }).collect(Collectors.toList())
                            : new ArrayList<>();

            dto.setItems(items);
            result.add(dto);
        }

        return result;
    }

    /* ===================================================
       PHẦN 3: THANH TOÁN (ORDER / TABLE)
       =================================================== */
    @Transactional
    public void processPayment(PaymentRequest request) {

        if (request.getOrderId() != null) {
            processSingleOrderPayment(request);
            return;
        }

        if (request.getTableId() != null) {
            processTablePayment(request);
            return;
        }

        throw new RuntimeException("PaymentRequest không hợp lệ");
    }

    /* ---------- THANH TOÁN 1 ORDER ---------- */
    private void processSingleOrderPayment(PaymentRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy Order ID " + request.getOrderId()));

        if (invoiceRepository.findByOrder_Id(order.getId()).isPresent()) {
            throw new RuntimeException("Order đã được thanh toán");
        }

        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setPaymentMethod(
                PaymentMethod.valueOf(request.getPaymentMethod())
        );
        invoice.setAmountPaid(request.getAmountPaid());
        invoice.setTransactionRef(request.getTransactionRef());
        invoice.setPaymentTime(LocalDateTime.now());

        invoiceRepository.save(invoice);

        order.setStatus(OrderStatus.PAID.name());
        orderRepository.save(order);

        releaseTableIfNeeded(order);
    }

    /* ---------- THANH TOÁN THEO BÀN ---------- */
    private void processTablePayment(PaymentRequest request) {

        Integer tableId = request.getTableId();
        Integer restaurantId = 1; // giữ nguyên logic hiện tại

        // Lấy toàn bộ order của nhà hàng
        List<Order> allOrders =
                orderRepository.findOrdersByRestaurantId(restaurantId);

        // Lọc theo bàn + chưa PAID
        List<Order> unpaidOrders = allOrders.stream()
                .filter(o ->
                        o.getRestaurantTable() != null &&
                                o.getRestaurantTable().getId().equals(tableId) &&
                                !OrderStatus.PAID.name().equals(o.getStatus())
                )
                .collect(Collectors.toList());

        if (unpaidOrders.isEmpty()) {
            throw new RuntimeException("Không có order chưa thanh toán trong bàn");
        }

        for (Order order : unpaidOrders) {

            if (invoiceRepository.findByOrder_Id(order.getId()).isPresent()) {
                continue;
            }

            Invoice invoice = new Invoice();
            invoice.setOrder(order);
            invoice.setPaymentMethod(
                    PaymentMethod.valueOf(request.getPaymentMethod())
            );
            invoice.setAmountPaid(order.getTotalAmount());
            invoice.setTransactionRef(request.getTransactionRef());
            invoice.setPaymentTime(LocalDateTime.now());

            invoiceRepository.save(invoice);

            order.setStatus(OrderStatus.PAID.name());
            orderRepository.save(order);
        }

        releaseTableById(tableId);
    }

    private void releaseTableIfNeeded(Order order) {
        if (order.getRestaurantTable() != null) {
            RestaurantTable table = order.getRestaurantTable();
            table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
            tableRepository.save(table);
        }
    }

    private void releaseTableById(Integer tableId) {
        tableRepository.findById(tableId).ifPresent(table -> {
            table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
            tableRepository.save(table);
        });
    }

    /* ===================================================
       PHẦN 4: CHI TIẾT ORDER + QR
       =================================================== */
    @Transactional(readOnly = true)
    public CashierOrderDetailDTO getOrderDetails(Integer orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy Order ID " + orderId));

        CashierOrderDetailDTO dto = new CashierOrderDetailDTO();
        dto.setOrderId(order.getId());

        dto.setTableName(
                order.getRestaurantTable() != null
                        ? order.getRestaurantTable().getTableName()
                        : "Mang về"
        );

        dto.setTotalAmount(
                order.getTotalAmount() != null
                        ? order.getTotalAmount()
                        : BigDecimal.ZERO
        );

        List<CashierOrderDetailDTO.DetailItem> items = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                CashierOrderDetailDTO.DetailItem i =
                        new CashierOrderDetailDTO.DetailItem();
                i.setProductName(
                        item.getProduct() != null
                                ? item.getProduct().getName()
                                : "Món đã xóa"
                );
                i.setQuantity(item.getQuantity());
                i.setUnitPrice(item.getUnitPrice());
                items.add(i);
            }
        }
        dto.setItems(items);

        try {
            if (order.getRestaurant() != null &&
                    order.getRestaurant().getBankQrConfig() != null) {

                dto.setBankConfig(
                        objectMapper.readValue(
                                order.getRestaurant().getBankQrConfig(),
                                CashierOrderDetailDTO.BankQrConfigDTO.class
                        )
                );
            }
        } catch (Exception e) {
            System.err.println(
                    "Lỗi parse bank_qr_config order " + orderId + ": " + e.getMessage());
        }

        return dto;
    }
}
