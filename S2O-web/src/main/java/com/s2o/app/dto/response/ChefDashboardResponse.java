package com.s2o.app.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ChefDashboardResponse {
    private ChefStats stats;
    private List<ChefOrderDTO> activeOrders;

    @Data
    public static class ChefStats {
        private long pending; // Chờ xử lý
        private long cooking; // Đang nấu
        private long ready;   // Sẵn sàng
    }

    @Data
    public static class ChefOrderDTO {
        private Integer id;
        private String tableName;
        private String orderTime;   // Format: HH:mm
        private String status;      // Trả về String cho frontend hiển thị logic
        private List<ChefOrderItemDTO> items;
    }

    @Data
    public static class ChefOrderItemDTO {
        private String productName;
        private int quantity;
        private String note;
    }
}