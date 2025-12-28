package com.example.s2o_mobile.data.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

    private int id;
    private int restaurantId;
    private int tableId;

    private List<OrderItem> items;

    private double totalAmount;
    private String status;

    public Order() {
    }

    public Order(int restaurantId, int tableId, List<OrderItem> items) {
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.items = items;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double calcTotalAmount() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.price * item.quantity;
        }
        return sum;
    }

    public static class OrderItem implements Serializable {
        int menuItemId;
        String name;
        double price;
        int quantity;
    }
}
