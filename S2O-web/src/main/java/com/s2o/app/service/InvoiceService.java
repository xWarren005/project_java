package com.s2o.app.service;

import com.s2o.app.dto.response.OrderHistoryResponse;
import com.s2o.app.entity.Order;
import com.s2o.app.entity.OrderItem;
import com.s2o.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private OrderRepository orderRepository;

    public List<OrderHistoryResponse> getCurrentSessionOrders(Integer userId) {
        // Lấy các trạng thái "đang ăn" (Trừ PAID và CANCELLED)
        List<String> activeStatuses = Arrays.asList("PENDING", "COOKING", "READY", "COMPLETED", "PAYMENT_PENDING");

        // Giả sử repository có hàm findByUserIdAndStatusIn
        // Hoặc dùng findAll() rồi filter như bên dưới (đơn giản nhưng chưa tối ưu nếu data lớn)
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getUserId() != null && o.getUserId().equals(userId))
                .filter(o -> activeStatuses.contains(o.getStatus()))
                .toList();

        return convertToDTO(orders);
    }

    // Reuse logic map DTO (có thể tách ra class Mapper riêng)
    private List<OrderHistoryResponse> convertToDTO(List<Order> orders) {
        List<OrderHistoryResponse> responseList = new ArrayList<>();
        for (Order o : orders) {
            OrderHistoryResponse dto = new OrderHistoryResponse();
            dto.setId(o.getId());
            dto.setTotalAmount(o.getTotalAmount());

            List<OrderHistoryResponse.ItemDTO> items = new ArrayList<>();
            if (o.getItems() != null) {
                for (OrderItem oi : o.getItems()) {
                    OrderHistoryResponse.ItemDTO itemDto = new OrderHistoryResponse.ItemDTO();
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