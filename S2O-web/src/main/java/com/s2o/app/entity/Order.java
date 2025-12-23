package com.s2o.app.entity;

import jakarta.persistence.*; // Hoặc javax.persistence nếu dùng Spring Boot cũ
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "restaurant_id")
    private Integer restaurantId;

    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "promotion_id")
    private Integer promotionId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    // Status: PENDING, CONFIRMED, SERVED, COMPLETED, CANCELLED
    @Column(name = "status")
    private String status;

    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}