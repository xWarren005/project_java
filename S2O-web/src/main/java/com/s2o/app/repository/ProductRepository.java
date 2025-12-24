package com.s2o.app.repository;

import com.s2o.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Query đếm số lượng món ăn theo nhà hàng (Dùng cho trang Overview)
    @Query(value = "SELECT COUNT(p.id) FROM products p " +
            "JOIN categories c ON p.category_id = c.id " +
            "WHERE c.restaurant_id = :restaurantId", nativeQuery = true)
    long countByRestaurantId(@Param("restaurantId") Integer restaurantId);
}