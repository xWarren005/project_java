package com.s2o.app.dto;

public class ActivityLogDTO {
    private String time;
    private String userEmail;
    private String action;
    private String detail;
    private String status;      // Thành công/Lỗi
    private String statusClass; // success/error

    public ActivityLogDTO(String time, String userEmail, String action, String detail, String status, String statusClass) {
        this.time = time;
        this.userEmail = userEmail;
        this.action = action;
        this.detail = detail;
        this.status = status;
        this.statusClass = statusClass;
    }

    // Getters...
    public String getTime() { return time; }
    public String getUserEmail() { return userEmail; }
    public String getAction() { return action; }
    public String getDetail() { return detail; }
    public String getStatus() { return status; }
    public String getStatusClass() { return statusClass; }
}