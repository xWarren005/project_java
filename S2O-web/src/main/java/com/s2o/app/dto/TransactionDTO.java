package com.s2o.app.dto;

public class TransactionDTO {
    private Long id;
    private String date;           // Dạng String "YYYY-MM-DD" cho đơn giản
    private String restaurant;     // Tên nhà hàng
    private String servicePackage; // Tên gói: Premium, Enterprise...
    private Double amount;         // Số tiền
    private Double commission;     // Hoa hồng

    public TransactionDTO(Long id, String date, String restaurant, String servicePackage, Double amount, Double commission) {
        this.id = id;
        this.date = date;
        this.restaurant = restaurant;
        this.servicePackage = servicePackage;
        this.amount = amount;
        this.commission = commission;
    }

    // Getters
    public Long getId() { return id; }
    public String getDate() { return date; }
    public String getRestaurant() { return restaurant; }
    public String getServicePackage() { return servicePackage; }
    public Double getAmount() { return amount; }
    public Double getCommission() { return commission; }
}