package com.s2o.app.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActivityLogDTO {
    private String time;
    private String user;
    private String action;
    private String details;
    private String status;      // Thành công/Lỗi
    private String statusClass; // success/error

    public ActivityLogDTO(String time, String userEmail, String action, String detail, String status, String statusClass) {
        this.time = time;
        this.user = userEmail;
        this.action = action;
        this.details = detail;
        this.status = status;
        this.statusClass = statusClass;
    }

}