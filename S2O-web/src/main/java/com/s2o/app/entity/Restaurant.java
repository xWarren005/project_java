package com.s2o.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "restaurants")
@Data
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    // Map đúng với cột 'phone_contact' trong SQL
    @Column(name = "phone_contact")
    private String phone;

    // Map đúng với cột 'avg_rating' trong SQL
    @Column(name = "avg_rating")
    private Double rating;

    // Map đúng với cột 'is_active' trong SQL
    @Column(name = "is_active")
    private Boolean isActive;

    // Cấu hình QR Code (Lưu chuỗi JSON)
    @Column(name = "bank_qr_config", columnDefinition = "json")
    private String bankQrConfig;

    // Các trường khác map tạm thời để tránh lỗi nếu hibernate validate
    @Column(name = "owner_id")
    private Long ownerId;
}