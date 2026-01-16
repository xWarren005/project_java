package com.s2o.app.dto;

public class RestaurantRevenueStat {
    private String restaurantName;
    private Double monthlyRevenue; // Tổng tiền nhà hàng bán được
    private Double commission;     // Hoa hồng hệ thống thu (5%)

    public RestaurantRevenueStat(String name, Double revenue) {
        this.restaurantName = name;
        this.monthlyRevenue = revenue != null ? revenue : 0.0;
        this.commission = this.monthlyRevenue * 0.05; // Tự động tính 5%
    }

    public String getRestaurantName() { return restaurantName; }
    public Double getMonthlyRevenue() { return monthlyRevenue; }
    public Double getCommission() { return commission; }
}