package com.example.s2o_mobile.data.model;

public class MenuItem {

    private int id;
    private String name;
    private int price;

    private String imageUrl;
    private String description;
    private String category;

    public MenuItem(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
