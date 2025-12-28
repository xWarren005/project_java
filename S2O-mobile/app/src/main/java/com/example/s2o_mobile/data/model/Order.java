package com.example.s2o_mobile.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {

    private int id;

    private int restaurantId;
    private int tableId;

    private List<OrderItem> items = new ArrayList<>();

    private double totalAmount;

    private String status;
    private String note;

    private String createdAt;

    public Order() {
    }

    public Order(int restaurantId, int tableId, List<OrderItem> items, double totalAmount) {
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        if (items != null) this.items = items;
        this.totalAmount = totalAmount;
    }

    public int getTotalQuantity() {
        int sum = 0;
        for (OrderItem it : items) {
            sum += it.getQuantity();
        }
        return sum;
    }

    public double calcTotalAmount() {
        double sum = 0;
        for (OrderItem it : items) {
            sum += it.getSubtotal();
        }
        return sum;
    }

    public static class OrderItem implements Serializable {

        private int menuItemId;
        private String name;
        private double price;
        private int quantity;
        private String imageUrl;

        public double getSubtotal() {
            return price * quantity;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
