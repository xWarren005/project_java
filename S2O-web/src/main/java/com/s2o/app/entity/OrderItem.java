package com.s2o.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Quan hệ cũ (Giữ nguyên để code cũ hoạt động bình thường)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // Quan hệ cũ (Giữ nguyên)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // ========================================================================
    // CODE MỚI THÊM: Read-only field để lấy ID sản phẩm
    // Giúp sửa lỗi "Cannot resolve method getProductId" bên Service
    // "insertable = false, updatable = false" -> Dùng chung cột product_id với biến 'product' ở trên
    // ========================================================================
    @Column(name = "product_id", insertable = false, updatable = false)
    private Integer productId;

    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    private String note;
}