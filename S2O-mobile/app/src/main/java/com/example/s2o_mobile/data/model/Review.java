package com.example.s2o_mobile.data.model;

import java.io.Serializable;

public class Review implements Serializable {

    private int id;
    private int restaurantId;
    private int userId;

    private int rating;
    private String content;

    public Review() {
    }

    public Review(int restaurantId, int userId, int rating, String content) {
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.rating = rating;
        this.content = content;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
