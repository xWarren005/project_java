package com.s2o.app.service;

import com.s2o.app.dto.response.OrderTrackingResponse;
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
public class GuestInvoiceService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Lấy hóa đơn tạm tính cho Khách vãng lai theo ID Bàn
     */
    public List<OrderTrackingResponse> getGuestInvoice(Integer tableId) {
        // Lấy các trạng thái "đang ăn" (Trừ PAID và CANCELLED)
        List<String> activeStatuses = Arrays.asList("PENDING", "COOKING", "READY", "COMPLETED", "PAYMENT_PENDING");

        List<Order> orders = orderRepository.findAll().stream()
                // --- THAY ĐỔI QUAN TRỌNG: Lọc theo TableId thay vì UserId ---
                .filter(o -> o.getTableId() != null && o.getTableId().equals(tableId))
                .filter(o -> activeStatuses.contains(o.getStatus()))
                // Sắp xếp đơn mới nhất lên đầu (Optional)
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());

        return convertToDTO(orders);
    }

    // Helper: Map Entity sang DTO (Giữ nguyên logic cũ)
    private List<OrderTrackingResponse> convertToDTO(List<Order> orders) {
        List<OrderTrackingResponse> responseList = new ArrayList<>();
        for (Order o : orders) {
            OrderTrackingResponse dto = new OrderTrackingResponse();
            dto.setId(o.getId());
            dto.setStatus(o.getStatus()); // Thêm dòng này để FE biết trạng thái
            dto.setTotalAmount(o.getTotalAmount());
            dto.setCreatedAt(o.getCreatedAt()); // Thêm ngày giờ nếu DTO có

            List<OrderTrackingResponse.ItemDTO> items = new ArrayList<>();
            if (o.getItems() != null) {
                for (OrderItem oi : o.getItems()) {
                    OrderTrackingResponse.ItemDTO itemDto = new OrderTrackingResponse.ItemDTO();
                    itemDto.setProductName(oi.getProduct() != null ? oi.getProduct().getName() : "Món xóa");
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
}