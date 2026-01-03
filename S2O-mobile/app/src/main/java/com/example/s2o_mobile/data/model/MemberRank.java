package com.example.s2o_mobile.data.model;

import java.io.Serializable;

public class MemberRank implements Serializable {

    private int id;
    private String code;
    private String name;
    private String description;

    private double discountPercent;
    private double pointMultiplier;

    public MemberRank() {
    }

    public MemberRank(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getPointMultiplier() {
        return pointMultiplier;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setPointMultiplier(double pointMultiplier) {
        this.pointMultiplier = pointMultiplier;
    }
}
