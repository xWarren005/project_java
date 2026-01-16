package com.s2o.app.service;

import com.fasterxml.jackson.databind.ObjectMapper; // [MỚI] Cần để parse JSON
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    // [MỚI] Inject ObjectMapper để xử lý JSON string từ DB
    @Autowired
    private ObjectMapper objectMapper;

    /* ===================================================
       PHẦN 1: QUẢN LÝ DANH SÁCH BÀN
       =================================================== */
    @Transactional(readOnly = true)
    public List<CashierTableDTO> getTablesForCashier(Integer restaurantId) {
        List<RestaurantTable> tables = tableRepository.findByRestaurantId(restaurantId);
        List<CashierTableDTO> result = new ArrayList<>();

        for (RestaurantTable table : tables) {
            CashierTableDTO dto = new CashierTableDTO();
            dto.setId(table.getId());
            dto.setName(table.getTableName());
            dto.setCapacity(table.getCapacity());

            try {
                Optional<Order> activeOrderOpt =
                        orderRepository.findFirstByTableIdAndStatusInOrderByCreatedAtDesc(
                                table.getId(),
                                List.of(
                                        OrderStatus.PENDING,
                                        OrderStatus.COOKING,
                                        OrderStatus.READY,
                                        OrderStatus.PAID
                                )
                        );

                if (activeOrderOpt.isPresent()) {
                    Order order = activeOrderOpt.get();
                    dto.setStatus("busy");

                    if (order.getCreatedAt() != null) {
                        long minutes = Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes();
                        dto.setTime(minutes + " phút trước");
                    } else {
                        dto.setTime("Vừa xong");
                    }

                    double totalMoney = 0.0;
                    if (order.getTotalAmount() != null) {
                        totalMoney = order.getTotalAmount().doubleValue();
                    }
                    dto.setTotal(totalMoney);
                    dto.setOrders(new ArrayList<>());

                } else {
                    String dbStatus = (table.getStatus() != null) ? table.getStatus().toString() : "AVAILABLE";
                    if ("RESERVED".equalsIgnoreCase(dbStatus)) {
                        dto.setStatus("reserved");
                        dto.setTime("Đã đặt trước");
                    } else {
                        dto.setStatus("empty");
                        dto.setTime("");
                    }
                    dto.setTotal(0.0);
                    dto.setOrders(new ArrayList<>());
                }
            } catch (Exception e) {
                dto.setStatus("empty");
                System.err.println("Error processing table " + table.getId());
            }
            result.add(dto);
        }
        return result;
    }

    /* ===================================================
       PHẦN 2: QUẢN LÝ DANH SÁCH HÓA ĐƠN
       =================================================== */
    @Transactional(readOnly = true)
    public List<CashierInvoiceDTO> getInvoicesForCashier() {
        Integer restaurantId = 1; // ID mặc định

        System.out.println("--- LOG: Bắt đầu lấy danh sách hóa đơn cho NH: " + restaurantId + " ---");

        List<Order> orders = orderRepository.findOrdersByRestaurantId(restaurantId);

        List<CashierInvoiceDTO> dtoList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        for (Order order : orders) {
            try {
                if (order == null) continue;

                CashierInvoiceDTO dto = new CashierInvoiceDTO();
                dto.setId("INV-" + String.format("%03d", order.getId()));
                dto.setOrderId(Long.valueOf(order.getId()));

                if (order.getRestaurantTable() != null) {
                    dto.setTable(order.getRestaurantTable().getTableName());
                } else {
                    dto.setTable("Mang về");
                }

                if (order.getCreatedAt() != null) {
                    dto.setTime(order.getCreatedAt().format(formatter));
                } else {
                    dto.setTime("N/A");
                }

                dto.setTotal(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);

                // Kiểm tra Invoice
                Optional<Invoice> invoiceOpt = invoiceRepository.findByOrder_Id(order.getId());

                if (invoiceOpt.isPresent()) {
                    dto.setStatus("PAID");
                    Invoice inv = invoiceOpt.get();

                    if (inv.getPaymentMethod() != null) {
                        String method = inv.getPaymentMethod().toString();
                        if (method.contains("CASH")) dto.setMethod("Tiền mặt");
                        else if (method.contains("BANK")) dto.setMethod("Chuyển khoản");
                        else dto.setMethod("Ví điện tử");
                    } else {
                        dto.setMethod("Khác");
                    }
                } else {
                    dto.setStatus("UNPAID");
                    dto.setMethod(null);
                }

                // Map danh sách món ăn
                List<CashierInvoiceDTO.ItemDTO> items = new ArrayList<>();
                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    items = order.getItems().stream().map(item -> {
                        CashierInvoiceDTO.ItemDTO itemDto = new CashierInvoiceDTO.ItemDTO();
                        if (item.getProduct() != null) {
                            itemDto.setName(item.getProduct().getName());
                        } else {
                            itemDto.setName("Món đã xóa");
                        }
                        itemDto.setPrice(item.getUnitPrice());
                        itemDto.setQty(item.getQuantity());
                        return itemDto;
                    }).collect(Collectors.toList());
                }
                dto.setItems(items);

                dtoList.add(dto);

            } catch (Exception e) {
                System.err.println("--- LOG ERROR: Lỗi xử lý đơn hàng ID " + order.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return dtoList;
    }

    /* ===================================================
       PHẦN 3: XỬ LÝ THANH TOÁN
       =================================================== */
    @Transactional
    public Invoice processPayment(PaymentRequest request) {
        // 1. Tìm đơn hàng
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + request.getOrderId()));

        // 2. Kiểm tra nếu đã có hóa đơn
        Optional<Invoice> existingInvoice = invoiceRepository.findByOrder_Id(order.getId());
        if (existingInvoice.isPresent()) {
            throw new RuntimeException("Order is already paid.");
        }

        // 3. Tạo hóa đơn (Invoice)
        Invoice invoice = new Invoice();
        invoice.setOrder(order);

        try {
            invoice.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment method: " + request.getPaymentMethod());
        }

        invoice.setAmountPaid(request.getAmountPaid());
        invoice.setTransactionRef(request.getTransactionRef());
        invoice.setPaymentTime(LocalDateTime.now());

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 4. Cập nhật trạng thái Order
        order.setStatus(OrderStatus.PAID.name());
        orderRepository.save(order);

        // 5. Giải phóng bàn
        if (order.getRestaurantTable() != null) {
            RestaurantTable table = order.getRestaurantTable();
            table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
            tableRepository.save(table);
        }

        return savedInvoice;
    }

    /* ===================================================
       PHẦN 4: LẤY CHI TIẾT ĐƠN HÀNG KÈM QR CONFIG
       =================================================== */
    @Transactional(readOnly = true)
    public CashierOrderDetailDTO getOrderDetails(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));

        CashierOrderDetailDTO dto = new CashierOrderDetailDTO();
        dto.setOrderId(order.getId());

        // Tên bàn
        if (order.getRestaurantTable() != null) {
            dto.setTableName(order.getRestaurantTable().getTableName());
        } else {
            dto.setTableName("Mang về");
        }

        // Tổng tiền
        dto.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);

        // Danh sách món
        List<CashierOrderDetailDTO.DetailItem> items = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                CashierOrderDetailDTO.DetailItem itemDto = new CashierOrderDetailDTO.DetailItem();
                if (item.getProduct() != null) {
                    itemDto.setProductName(item.getProduct().getName());
                } else {
                    itemDto.setProductName("Món đã xóa");
                }
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getUnitPrice());
                items.add(itemDto);
            }
        }
        dto.setItems(items);

        // [MỚI] Xử lý Bank QR Config từ bảng Restaurant (JSON -> Object)
        try {
            if (order.getRestaurant() != null && order.getRestaurant().getBankQrConfig() != null) {
                String jsonConfig = order.getRestaurant().getBankQrConfig();
                // Parse JSON String từ DB thành Object DTO
                CashierOrderDetailDTO.BankQrConfigDTO bankConfig =
                        objectMapper.readValue(jsonConfig, CashierOrderDetailDTO.BankQrConfigDTO.class);
                dto.setBankConfig(bankConfig);
            }
        } catch (Exception e) {
            // Chỉ log lỗi, không throw exception để tránh chết API nếu JSON sai
            System.err.println("--- LOG WARNING: Lỗi parse bank_qr_config cho Order " + orderId + ": " + e.getMessage());
        }

        return dto;
    }
}