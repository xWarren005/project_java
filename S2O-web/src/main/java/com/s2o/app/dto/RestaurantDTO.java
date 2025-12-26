package com.s2o.app.dto;

public class RestaurantDTO {
    private String name;
    private String address;
    private String timeAgo;     // Ví dụ: "Vừa truy cập"
    private String status;      // "Hoạt động" hoặc "Đóng cửa"
    private String statusClass; // "success" hoặc "error" để tô màu

    public RestaurantDTO(String name, String address, String timeAgo, String status, String statusClass) {
        this.name = name;
        this.address = address;
        this.timeAgo = timeAgo;
        this.status = status;
        this.statusClass = statusClass;
    }

    // Getters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getTimeAgo() { return timeAgo; }
    public String getStatus() { return status; }
    public String getStatusClass() { return statusClass; }
}