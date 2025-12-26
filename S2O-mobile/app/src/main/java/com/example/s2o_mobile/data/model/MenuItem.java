package com.example.s2o_mobile.data.model;

import java.io.Serializable;

public class MenuItem implements Serializable {

    private int id;
    private String name;
    private int price;

    private String imageUrl;
    private String description;
    private String category;

    public MenuItem(int id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "MenuItem{id=" + id + ", name='" + name + "'}";
    }
}
