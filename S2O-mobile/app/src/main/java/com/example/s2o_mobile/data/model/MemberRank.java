package com.example.s2o_mobile.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MemberRank implements Serializable {

    private int id;
    private String code;
    private String name;
    private String description;

    private double discountPercent;
    private double pointMultiplier;
    private double minTotalSpend;

    private List<String> benefits = new ArrayList<>();

    public MemberRank() {
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

    public double getMinTotalSpend() {
        return minTotalSpend;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setPointMultiplier(double pointMultiplier) {
        this.pointMultiplier = pointMultiplier;
    }

    public void setMinTotalSpend(double minTotalSpend) {
        this.minTotalSpend = minTotalSpend;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }
}
