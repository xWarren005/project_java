package com.s2o.app.dto;

import java.util.List;

public class RevenueDTO {
    // 3 chỉ số thống kê tổng quan (Hệ thống thu về)
    private Double systemRevenueToday;
    private Double systemRevenueMonth;
    private Double systemRevenueYear;

    // Danh sách chi tiết từng nhà hàng
    private List<RestaurantRevenueStat> restaurantStats;

    public RevenueDTO(Double today, Double month, Double year, List<RestaurantRevenueStat> list) {
        this.systemRevenueToday = today;
        this.systemRevenueMonth = month;
        this.systemRevenueYear = year;
        this.restaurantStats = list;
    }

    public Double getSystemRevenueToday() { return systemRevenueToday; }
    public Double getSystemRevenueMonth() { return systemRevenueMonth; }
    public Double getSystemRevenueYear() { return systemRevenueYear; }
    public List<RestaurantRevenueStat> getRestaurantStats() { return restaurantStats; }
}