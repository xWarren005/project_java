package com.s2o.app.service;

import com.s2o.app.dto.response.ChefDashboardResponse;
import com.s2o.app.entity.Order;
import com.s2o.app.entity.OrderStatus;
import com.s2o.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChefService {

    @Autowired
    private OrderRepository orderRepository;

    public ChefDashboardResponse getDashboardData(Integer restaurantId) {
        ChefDashboardResponse response = new ChefDashboardResponse();

        // 1. Lấy thống kê (Stats)
        // Convert Enum -> String để khớp với cấu trúc DB cũ (cột status là varchar)
        ChefDashboardResponse.ChefStats stats = new ChefDashboardResponse.ChefStats();
        stats.setPending(orderRepository.countByRestaurantIdAndStatus(restaurantId, OrderStatus.PENDING.name()));
        stats.setCooking(orderRepository.countByRestaurantIdAndStatus(restaurantId, OrderStatus.COOKING.name()));
        stats.setReady(orderRepository.countByRestaurantIdAndStatus(restaurantId, OrderStatus.READY.name()));
        response.setStats(stats);

        // 2. Lấy danh sách đơn hàng đang hoạt động (Active Orders)
        // Chỉ lấy các trạng thái mà Bếp quan tâm
        List<String> activeStatuses = Arrays.asList(
                OrderStatus.PENDING.name(),
                OrderStatus.COOKING.name(),
                OrderStatus.READY.name()
        );

        // Gọi Repository (lưu ý: hàm findChefOrders phải nhận List<String> nếu DB lưu status dạng chuỗi)
        List<Order> orders = orderRepository.findChefOrders(restaurantId, activeStatuses);

        // 3. Convert Entity sang DTO để trả về cho Frontend
        List<ChefDashboardResponse.ChefOrderDTO> orderDTOs = orders.stream().map(order -> {
            ChefDashboardResponse.ChefOrderDTO dto = new ChefDashboardResponse.ChefOrderDTO();
            dto.setId(order.getId());

            // Trả về status nguyên bản (dạng String) khớp với Enum
            dto.setStatus(order.getStatus());

            // Xử lý tên bàn (nếu null thì gán mặc định là Mang về / Online)
            if (order.getRestaurantTable() != null) {
                dto.setTableName(order.getRestaurantTable().getTableName());
            } else {
                dto.setTableName("Mang về / Online");
            }

            // Format thời gian hiển thị (VD: 10:30)
            if (order.getCreatedAt() != null) {
                dto.setOrderTime(order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")));
            }

            // Map danh sách món ăn
            List<ChefDashboardResponse.ChefOrderItemDTO> items = order.getItems().stream().map(item -> {
                ChefDashboardResponse.ChefOrderItemDTO itemDto = new ChefDashboardResponse.ChefOrderItemDTO();

                // Logic lấy tên món: Ưu tiên lấy từ object Product
                if (item.getProduct() != null) {
                    itemDto.setProductName(item.getProduct().getName());
                } else {
                    // Nếu Product null (do lười load), fallback về ID lấy từ field mới thêm
                    itemDto.setProductName("Món #" + item.getProductId());
                }

                itemDto.setQuantity(item.getQuantity());
                itemDto.setNote(item.getNote());
                return itemDto;
            }).collect(Collectors.toList());

            dto.setItems(items);
            return dto;
        }).collect(Collectors.toList());

        response.setActiveOrders(orderDTOs);
        return response;
    }

    // Logic đổi trạng thái khi bấm nút trên màn hình Bếp
    @Transactional
    public void updateOrderStatus(Integer orderId, String newStatusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại: " + orderId));

        try {
            // Validate: Kiểm tra xem chuỗi gửi lên có đúng là Enum hợp lệ không
            OrderStatus newStatus = OrderStatus.valueOf(newStatusStr);

            // Lưu vào DB dưới dạng String (để tương thích DB cũ)
            order.setStatus(newStatus.name());
            orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + newStatusStr);
        }
    }
}