package com.s2o.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "restaurant_id")
    private Integer restaurantId;

    private String name;

    @Column(name = "display_order")
    private Integer displayOrder;

    // Quan hệ: 1 Danh mục có nhiều Món ăn
    @OneToMany(mappedBy = "category")
    @JsonIgnore // Ngăn lỗi vòng lặp vô tận khi chuyển sang JSON
    private List<Product> products;
}