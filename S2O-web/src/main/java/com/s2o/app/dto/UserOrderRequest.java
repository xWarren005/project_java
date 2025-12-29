package com.s2o.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserOrderRequest {
    private Integer restaurantId; // ID nhà hàng khách đang xem
    private Integer tableId;      // Có thể null nếu đặt mang về
    private String note;
    private List<OrderItemRequest> items;
    @Data
    public static class OrderItemRequest {
        private Integer productId;
        private Integer quantity;
}
}