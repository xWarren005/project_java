package com.example.s2o_mobile.data.model;

import java.io.Serializable;

public class Booking implements Serializable {

    private int Id;
    private int restaurantId;
    private String restaurantName;

    private String bookingTime;
    private String guestCount;

    private String status;
    private String note;
    private String created_at;

    public Booking() {
    }

    public Booking(int Id, int restaurantId, String bookingTime, String status) {
        Id = Id;
        this.restaurantId = restaurantId;
        this.bookingTime = bookingTime;
        this.status = status;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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

    public String getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(String guestCount) {
        guestCount = guestCount;
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
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }
}
