package com.example.s2o_mobile.data.model;

public class Restaurant {

    private int id;
    private String name;
    private String address;
    private String imageUrl;
    private String description;
    private double avgRating;

    public Restaurant() {
    }

    public Restaurant(int id, String name, String address,
                      String imageUrl, String description, double avgRating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.description = description;
        this.avgRating = avgRating;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
}
