package com.example.s2o_mobile.data.model;

import java.io.Serializable;

public class Review implements Serializable {

    private int id;
    private int restaurantId;

    private int userId;
    private String userName;
    private String userAvatarUrl;

    private int rating;
    private String content;

    private String createdAt;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1) rating = 1;
        if (rating > 5) rating = 5;
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
