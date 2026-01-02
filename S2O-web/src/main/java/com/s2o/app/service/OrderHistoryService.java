package com.s2o.app.service;
import com.s2o.app.dto.response.OrderHistoryResponse;
import com.s2o.app.entity.Order;
import com.s2o.app.entity.OrderItem;
import com.s2o.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderHistoryService {
    @Autowired
    private OrderRepository orderRepository;

    // Lấy danh sách đơn hàng của User (Sắp xếp mới nhất trước)
    public List<OrderHistoryResponse> getUserOrderHistory(Integer userId) {

        // 1. Lấy dữ liệu thô từ DB
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getUserId() != null && o.getUserId().equals(userId))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())) // Mới nhất lên đầu
                .collect(Collectors.toList());

        // 2. Chuyển đổi sang DTO
        List<OrderHistoryResponse> responseList = new ArrayList<>();
        for (Order o : orders) {
            OrderHistoryResponse dto = new OrderHistoryResponse();
            dto.setId(o.getId());
            dto.setStatus(o.getStatus());
            dto.setStatusVietnamese(mapStatusToVN(o.getStatus()));
            dto.setTotalAmount(o.getTotalAmount());
            dto.setCreatedAt(o.getCreatedAt());

            List<OrderHistoryResponse.ItemDTO> items = new ArrayList<>();
            if (o.getItems() != null) {
                for (OrderItem oi : o.getItems()) {
                    OrderHistoryResponse.ItemDTO itemDto = new OrderHistoryResponse.ItemDTO();
                    itemDto.setProductName(oi.getProduct() != null ? oi.getProduct().getName() : "Món đã xóa");
                    itemDto.setQuantity(oi.getQuantity());
                    itemDto.setUnitPrice(oi.getUnitPrice());
                    items.add(itemDto);
                }
            }
            dto.setItems(items);
            responseList.add(dto);
        }
        return responseList;
    }

    // Helper: Dịch trạng thái
    private String mapStatusToVN(String status) {
        if (status == null) return "Không rõ";
        switch (status) {
            case "PENDING":     return "Chờ xác nhận";   // Mới đặt
            case "COOKING":     return "Đang chế biến";  // Bếp đã bấm "Bắt đầu nấu"
            case "READY":       return "Món đã xong";    // Bếp đã bấm "Sẵn sàng"
            case "COMPLETED":   return "Đã phục vụ";     // Bếp/Nhân viên bấm "Hoàn thành"
            case "CANCELLED":   return "Đã hủy";
            case "PAID":        return "Đã thanh toán";
            default:            return status;
        }
    }
}
