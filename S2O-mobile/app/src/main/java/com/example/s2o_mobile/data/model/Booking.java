package com.example.s2o_mobile.data.model;

import java.io.Serializable;

public class Booking implements Serializable {

    private int id;

    private int restaurantId;
    private String restaurantName;

    private String bookingTime;
    private int guestCount;

    private String status;
    private String note;

    private String createdAt;

    public Booking() {}

    public Booking(int id, int restaurantId, String bookingTime, int guestCount, String status) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.bookingTime = bookingTime;
        this.guestCount = guestCount;
        this.status = status;
    }

    // ===== Getter / Setter =====
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

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
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

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", restaurantId=" + restaurantId +
                ", restaurantName='" + restaurantName + '\'' +
                ", bookingTime='" + bookingTime + '\'' +
                ", guestCount=" + guestCount +
                ", status='" + status + '\'' +
                ", note='" + note + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
