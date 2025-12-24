package com.s2o.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Thay thế "Integer categoryId" bằng Object Category để đúng chuẩn JPA
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "ai_generated")
    private Boolean aiGenerated;

    // --- Helper Methods cho Frontend ---

    // Giúp Frontend lấy tên danh mục dễ dàng (dish.categoryName)
    public String getCategoryName() {
        return category != null ? category.getName() : "";
    }

    // Giúp Frontend lấy ID danh mục dễ dàng (dish.categoryId)
    public Integer getCategoryId() {
        return category != null ? category.getId() : null;
    }
}