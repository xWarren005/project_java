package com.s2o.app.service;

import com.s2o.app.dto.response.CashierInvoiceDTO;
import com.s2o.app.dto.response.CashierTableDTO;
import com.s2o.app.entity.Invoice;
import com.s2o.app.entity.Order;
import com.s2o.app.entity.OrderStatus;
import com.s2o.app.entity.OrderItem;
import com.s2o.app.entity.RestaurantTable;
import com.s2o.app.repository.InvoiceRepository;
import com.s2o.app.repository.OrderRepository;
import com.s2o.app.repository.TableRepository;
import com.s2o.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDateTime;
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

    /* ===================================================
       PHẦN 1: QUẢN LÝ DANH SÁCH BÀN (GIỮ NGUYÊN)
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
                                        OrderStatus.COMPLETED
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
                    dto.setOrders(new ArrayList<>()); // Rút gọn để tối ưu hiệu năng

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
       PHẦN 2: QUẢN LÝ DANH SÁCH HÓA ĐƠN (ĐÃ SỬA)
       =================================================== */
    @Transactional(readOnly = true) // QUAN TRỌNG: Để Hibernate tự động lấy Items/Table
    public List<CashierInvoiceDTO> getInvoicesForCashier() {
        // 1. Gán cứng ID = 1 để test (Đảm bảo luôn có dữ liệu nếu DB có data)
        Integer restaurantId = 1;

        System.out.println("--- LOG: Bắt đầu lấy danh sách hóa đơn cho NH: " + restaurantId + " ---");

        // 2. Lấy danh sách Order
        List<Order> orders = orderRepository.findOrdersByRestaurantId(restaurantId);
        System.out.println("--- LOG: Tìm thấy " + orders.size() + " đơn hàng trong DB ---");

        List<CashierInvoiceDTO> dtoList = new ArrayList<>();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        for (Order order : orders) {
            try {
                // Kiểm tra an toàn
                if (order == null) continue;

                CashierInvoiceDTO dto = new CashierInvoiceDTO();
                dto.setId("INV-" + String.format("%03d", order.getId()));
                dto.setOrderId(Long.valueOf(order.getId()));

                // [FIX] Xử lý Bàn (Nếu null thì là Mang về)
                if (order.getRestaurantTable() != null) {
                    dto.setTable(order.getRestaurantTable().getTableName());
                } else {
                    dto.setTable("Mang về");
                }

                // [FIX] Xử lý Thời gian
                if (order.getCreatedAt() != null) {
                    dto.setTime(order.getCreatedAt().format(formatter));
                } else {
                    dto.setTime("N/A");
                }

                dto.setTotal(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);

                // 3. Kiểm tra Invoice (PAID/UNPAID)
                Optional<Invoice> invoiceOpt = invoiceRepository.findByOrder_Id(order.getId());

                if (invoiceOpt.isPresent()) {
                    dto.setStatus("PAID"); // In hoa
                    Invoice inv = invoiceOpt.get();

                    // Map Payment Method an toàn
                    if (inv.getPaymentMethod() != null) {
                        String method = inv.getPaymentMethod().toString();
                        if (method.contains("CASH")) dto.setMethod("Tiền mặt");
                        else if (method.contains("BANK")) dto.setMethod("Chuyển khoản");
                        else dto.setMethod("Ví điện tử");
                    } else {
                        dto.setMethod("Khác");
                    }
                } else {
                    dto.setStatus("UNPAID"); // In hoa
                    dto.setMethod(null);
                }

                // 4. Map danh sách món ăn (Sẽ tự động fetch từ DB nhờ @Transactional)
                List<CashierInvoiceDTO.ItemDTO> items = new ArrayList<>();
                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    items = order.getItems().stream().map(item -> {
                        CashierInvoiceDTO.ItemDTO itemDto = new CashierInvoiceDTO.ItemDTO();
                        // Kiểm tra nếu sản phẩm bị xóa
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
                // Log lỗi ra console để debug, nhưng KHÔNG làm sập API
                System.err.println("--- LOG ERROR: Lỗi xử lý đơn hàng ID " + order.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("--- LOG: Trả về " + dtoList.size() + " hóa đơn cho Frontend ---");
        return dtoList;
    }
}