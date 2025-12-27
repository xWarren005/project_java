package com.s2o.app.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ProfileResponse {
    private UserInfoDTO user;
    private List<CalendarItemDTO> calendar;
    private List<VoucherItemDTO> vouchers; // Có thể mở rộng sau
    @Data
    public static class UserInfoDTO {
        private String fullname;
        private String email;
        private String avatar;
        private String rank;
    }
    @Data
    public static class CalendarItemDTO {
        private String id;
        private String place;      // Tên nhà hàng
        private String date;       // VD: 25/12/2025
        private String time;       // VD: 19:30
        private String status;     // upcoming/finished
        private String statusText; // Sắp tới/Hoàn thành
    }
    @Data
    public static class VoucherItemDTO{
        private String id;
        private String title;
        private String code;
        private String discount;
        private String expiry;
        private boolean isUsed;
    }
}

