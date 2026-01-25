package com.s2o.app.dto;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }
}