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

    public RestaurantDTO() {
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }
}