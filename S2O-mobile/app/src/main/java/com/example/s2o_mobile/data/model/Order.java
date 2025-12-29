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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = (items == null) ? new ArrayList<>() : items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalQuantity() {
        int sum = 0;
        if (items == null) return 0;
        for (OrderItem it : items) {
            if (it != null) sum += it.getQuantity();
        }
        return sum;
    }

    public double calcTotalAmount() {
        double sum = 0.0;
        if (items == null) return 0.0;
        for (OrderItem it : items) {
            if (it != null) sum += it.getSubtotal();
        }
        return sum;
    }

    public static class OrderItem implements Serializable {

        private int menuItemId;
        private String name;
        private double price;
        private int quantity;

        private String imageUrl;

        public OrderItem() {
        }

        public OrderItem(int menuItemId, String name, double price, int quantity) {
            this.menuItemId = menuItemId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public int getMenuItemId() {
            return menuItemId;
        }

        public void setMenuItemId(int menuItemId) {
            this.menuItemId = menuItemId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public double getSubtotal() {
            return price * quantity;
        }
    }
}
