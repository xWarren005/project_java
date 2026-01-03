package com.s2o.app.service;

import com.s2o.app.dto.response.OrderTrackingResponse; // Dùng DTO của Guest
import com.s2o.app.entity.Order;
import com.s2o.app.entity.OrderItem;
import com.s2o.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestOrderTrackingService {

    @Autowired
    private OrderRepository orderRepository;

    // --- HÀM 1: Lấy danh sách để Theo Dõi (Tracking) ---
    // Thay vì userId, Guest dùng tableId
    public List<OrderTrackingResponse> getOrdersByTableId(Integer tableId) {

        // Logic: Lấy tất cả đơn của bàn trong phiên hiện tại
        // (Bao gồm cả đang nấu, chờ thanh toán, và đã thanh toán)
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getTableId() != null && o.getTableId().equals(tableId))
                // Sắp xếp: Mới nhất lên đầu
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());

        return convertToDTO(orders);
    }

    // --- HÀM 2: Lấy danh sách Đã Thanh Toán (History/Invoice) ---
    // (Dành cho trường hợp muốn xem lịch sử các đơn đã chốt tiền)
    public List<OrderTrackingResponse> getPaidOrdersByTableId(Integer tableId) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getTableId() != null && o.getTableId().equals(tableId))
                .filter(o -> "PAID".equals(o.getStatus())) // <--- CHỈ LẤY ĐÃ TRẢ TIỀN
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());

        return convertToDTO(orders);
    }

    // Helper: Chuyển đổi Entity sang Guest DTO (OrderTrackingResponse)
    private List<OrderTrackingResponse> convertToDTO(List<Order> orders) {
        List<OrderTrackingResponse> responseList = new ArrayList<>();

        for (Order o : orders) {
            OrderTrackingResponse dto = new OrderTrackingResponse();
            dto.setId(o.getId());
            dto.setStatus(o.getStatus());
            dto.setStatusVietnamese(mapStatusToVN(o.getStatus()));
            dto.setTotalAmount(o.getTotalAmount());
            dto.setCreatedAt(o.getCreatedAt());

            List<OrderTrackingResponse.ItemDTO> items = new ArrayList<>();
            if (o.getItems() != null) {
                for (OrderItem oi : o.getItems()) {
                    OrderTrackingResponse.ItemDTO itemDto = new OrderTrackingResponse.ItemDTO();
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

    // Helper: Dịch trạng thái (Thêm trạng thái Guest)
    private String mapStatusToVN(String status) {
        if (status == null) return "Không rõ";
        switch (status) {
            case "PENDING":         return "Chờ xác nhận";
            case "COOKING":         return "Đang chế biến";
            case "READY":           return "Món đã xong";
            case "COMPLETED":       return "Đã phục vụ"; // Đã ra món
            case "PAYMENT_PENDING": return "Chờ thu ngân";  // Guest đặc biệt cần cái này
            case "PAID":            return "Đã thanh toán";
            case "CANCELLED":       return "Đã hủy";
            default:                return status;
        }
    }
}