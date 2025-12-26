package com.s2o.app.dto;

public class StatItem {
    private String label;      // Ví dụ: "Nhà hàng Hoạt động"
    private String value;      // Ví dụ: "248"
    private String trend;      // Ví dụ: "+12.5%"
    private boolean isUp;      // true = tăng (màu xanh), false = giảm (màu đỏ)
    private String icon;       // Tên icon lucide (file-text, users...)
    private String colorClass; // Class màu (icon-blue, icon-purple...)

    // Constructor
    public StatItem(String label, String value, String trend, boolean isUp, String icon, String colorClass) {
        this.label = label;
        this.value = value;
        this.trend = trend;
        this.isUp = isUp;
        this.icon = icon;
        this.colorClass = colorClass;
    }

    // Getters và Setters (Bắt buộc phải có để Thymeleaf đọc được)
    public String getLabel() { return label; }
    public String getValue() { return value; }
    public String getTrend() { return trend; }
    public boolean isUp() { return isUp; }
    public String getIcon() { return icon; }
    public String getColorClass() { return colorClass; }
}