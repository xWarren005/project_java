package com.s2o.app.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class CashierTableDTO {
    private Integer id;
    private String name;        // VD: "Bàn #1"
    private int capacity;       // Sức chứa
    private String status;      // "empty", "busy", "reserved"
    private String time;        // time
    private Double total;       // Tổng tiền tạm tính
    private List<CashierOrderItemDTO> orders; // Danh sách món

    @Data
    public static class CashierOrderItemDTO {
        private String name;
        private int qty;
    }
}