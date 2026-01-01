package com.s2o.app.entity;

public enum OrderStatus {
    PENDING,    // Chờ xử lý
    COOKING,    // Đang nấu
    READY,      // Sẵn sàng
    COMPLETED,  // Đã phục vụ
    CANCELLED   // Đã hủy
}