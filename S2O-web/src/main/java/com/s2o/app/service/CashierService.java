package com.s2o.app.service;

import com.s2o.app.dto.response.CashierTableDTO;
import com.s2o.app.entity.Order;
import com.s2o.app.entity.OrderItem;
import com.s2o.app.entity.RestaurantTable;
import com.s2o.app.repository.OrderRepository;
import com.s2o.app.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional(readOnly = true) // Thêm transactional để đảm bảo load được các relationship Lazy (như items, product)
    public List<CashierTableDTO> getTablesForCashier(Integer restaurantId) {
        List<RestaurantTable> tables = tableRepository.findByRestaurantId(restaurantId);
        List<CashierTableDTO> result = new ArrayList<>();

        for (RestaurantTable table : tables) {
            CashierTableDTO dto = new CashierTableDTO();
            dto.setId(table.getId());
            dto.setName(table.getTableName()); // DB column: table_name
            dto.setCapacity(table.getCapacity());

            // 1. Kiểm tra xem bàn có đơn hàng nào đang Active không (Chưa thanh toán)
            Optional<Order> activeOrderOpt = orderRepository.findActiveOrderByTableId(table.getId());

            if (activeOrderOpt.isPresent()) {
                Order order = activeOrderOpt.get();

                // --- TRƯỜNG HỢP: CÓ KHÁCH (BUSY) ---
                dto.setStatus("busy");

                // Tính thời gian: "12 phút trước"
                if (order.getCreatedAt() != null) {
                    long minutes = Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes();
                    dto.setTime(minutes + " phút trước");
                } else {
                    dto.setTime("Vừa xong");
                }

                // Tính Tổng tiền (Logic: Ưu tiên TotalAmount trong Order, nếu 0 thì tự cộng từ Items)
                double totalMoney = 0.0;
                if (order.getTotalAmount() != null && order.getTotalAmount().doubleValue() > 0) {
                    totalMoney = order.getTotalAmount().doubleValue();
                } else {
                    // Fallback: Tự tính tổng tiền từ danh sách món nếu Order chưa update total
                    if (order.getItems() != null) {
                        for (OrderItem item : order.getItems()) {
                            double price = (item.getUnitPrice() != null) ? item.getUnitPrice().doubleValue() : 0.0;
                            totalMoney += price * item.getQuantity();
                        }
                    }
                }
                dto.setTotal(totalMoney);

                // Map danh sách món ăn ra DTO
                List<CashierTableDTO.CashierOrderItemDTO> itemDTOs = new ArrayList<>();
                if (order.getItems() != null) {
                    itemDTOs = order.getItems().stream().map(item -> {
                        CashierTableDTO.CashierOrderItemDTO itemDto = new CashierTableDTO.CashierOrderItemDTO();

                        // Lấy tên món an toàn
                        if (item.getProduct() != null) {
                            itemDto.setName(item.getProduct().getName());
                        } else {
                            // Trường hợp hiếm: ko lấy được product, hiển thị ID tạm
                            // Nếu bạn đã thêm field productId vào OrderItem như bài trước thì dùng item.getProductId()
                            // Nếu chưa thì dùng item.getId() của chính OrderItem
                            itemDto.setName("Món (Đang tải...)");
                        }

                        itemDto.setQty(item.getQuantity());
                        return itemDto;
                    }).collect(Collectors.toList());
                }
                dto.setOrders(itemDTOs);

            } else {
                // --- TRƯỜNG HỢP: KHÔNG CÓ ĐƠN (CHECK TRẠNG THÁI BÀN GỐC) ---
                // DB status là Enum: AVAILABLE, OCCUPIED, RESERVED
                String dbStatus = (table.getStatus() != null) ? table.getStatus().toString() : "AVAILABLE";

                if ("RESERVED".equalsIgnoreCase(dbStatus)) {
                    dto.setStatus("reserved");
                    dto.setTime("Đã đặt trước");
                } else {
                    dto.setStatus("empty"); // Mặc định là Trống
                    dto.setTime("");
                }

                // Các trường khác reset về 0/null
                dto.setTotal(0.0);
                dto.setOrders(new ArrayList<>());
            }
            result.add(dto);
        }
        return result;
    }
}