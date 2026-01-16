package com.example.s2o_mobile.data.model;

public class HistoryRow {

    private long timeMillis;
    private Double amount;
    private String title;

    public HistoryRow() {
    }

    public HistoryRow(long timeMillis, Double amount, String title) {
        this.timeMillis = timeMillis;
        this.amount = amount;
        this.title = title;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
