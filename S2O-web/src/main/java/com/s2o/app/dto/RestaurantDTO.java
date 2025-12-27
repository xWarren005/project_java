package com.s2o.app.dto;

public class RestaurantDTO {
    // --- CÁC TRƯỜNG MỚI (Dùng cho trang Quản lý) ---
    private Long id;
    private Double rating;

    // --- CÁC TRƯỜNG CŨ (Dùng chung) ---
    private String name;
    private String address;
    private String status;      // "active", "pending", "inactive" hoặc "Hoạt động"...

    // --- CÁC TRƯỜNG CHO DASHBOARD (Optional) ---
    private String timeAgo;
    private String statusClass;

    // CONSTRUCTOR 1: Dùng cho trang Quản lý Nhà hàng (Đầy đủ ID, Rating)
    public RestaurantDTO(Long id, String name, String address, String status, Double rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.status = status;
        this.rating = rating;
    }

    // CONSTRUCTOR 2: Dùng cho Dashboard
    public RestaurantDTO(String name, String address, String timeAgo, String status, String statusClass) {
        this.name = name;
        this.address = address;
        this.timeAgo = timeAgo;
        this.status = status;
        this.statusClass = statusClass;
    }

    // --- GETTERS  ---
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getStatus() { return status; }
    public Double getRating() { return rating; }
    public String getTimeAgo() { return timeAgo; }
    public String getStatusClass() { return statusClass; }
}