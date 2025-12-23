package com.example.s2o_mobile.data.model;

import java.io.Serializable;

public class Booking implements Serializable {

    private int id;
    private int restaurantId;
    private String bookingTime;
    private String guestCount;

    public Booking() {
    }

    public Booking(int id, int restaurantId, String bookingTime) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.bookingTime = bookingTime;
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
        this.guestCount = guestCount;
    }
}
