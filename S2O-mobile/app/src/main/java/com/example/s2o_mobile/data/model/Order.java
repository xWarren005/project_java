package com.example.s2o_mobile.data.model;

import java.io.Serializable;

public class Order implements Serializable {

    private int id;
    private int restaurantId;

    private double totalAmount;

    public Order() {
    }

    public Order(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getId() {
        return id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
