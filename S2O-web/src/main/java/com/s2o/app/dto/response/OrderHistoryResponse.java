package com.s2o.app.dto.response;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data

public class OrderHistoryResponse {
    private Integer id;
    private String status;           // Trạng thái gốc (PENDING, CONFIRMED...)
    private String statusVietnamese; // Trạng thái hiển thị (Chờ xác nhận, Đang nấu...)
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<ItemDTO> items;
    @Data
    public static class ItemDTO {
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
