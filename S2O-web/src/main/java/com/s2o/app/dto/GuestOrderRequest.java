package com.s2o.app.dto;
import lombok.Data;
import java.util.List;

@Data
public class GuestOrderRequest {
    private Integer tableId;       // Khách vãng lai định danh bằng ID bàn
    private Integer restaurantId;
    private String note;// ID nhà hàng (lấy từ localStorage gửi lên)
    private List<GuestOrderItem> items;

    @Data
    public static class GuestOrderItem {
        private Integer productId;
        private Integer quantity;
        private String note;
    }
}
