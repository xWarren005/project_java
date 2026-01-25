package com.s2o.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "promotion_id")
    private Integer promotionId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "status")
    private String status;

    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    // ========================================================================
    // CODE MỚI THÊM CHO CHEF DASHBOARD
    // ========================================================================

    // Mapping thêm quan hệ để Chef lấy tên bàn.
    // "insertable = false, updatable = false" nghĩa là field này chỉ để ĐỌC dữ liệu,
    // việc thêm sửa vẫn thông qua field "tableId" ở trên -> An toàn tuyệt đối cho code cũ.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", insertable = false, updatable = false)
    private RestaurantTable restaurantTable;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", insertable = false, updatable = false)
    private Restaurant restaurant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}