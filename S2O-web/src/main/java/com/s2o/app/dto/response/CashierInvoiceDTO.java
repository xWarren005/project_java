package com.s2o.app.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CashierInvoiceDTO {
    private String id;           // Ví dụ: "INV-001" (Là Order ID hoặc Invoice ID)
    private Long orderId;        // ID gốc trong DB để xử lý logic
    private String table;        // Tên bàn (VD: "2")
    private String status;       // "paid" hoặc "unpaid"
    private String time;         // Format: "12:30 20/12/2025"
    private String method;       // "Tiền mặt", "Chuyển khoản" (null nếu unpaid)
    private BigDecimal total;    // Tổng tiền
    private List<ItemDTO> items; // Danh sách món

    @Data
    public static class ItemDTO {
        private String name;
        private BigDecimal price;
        private int qty;
    }
}