package com.s2o.app.repository;

import com.s2o.app.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Integer> {
    // Lấy danh sách bàn theo ID nhà hàng
    List<RestaurantTable> findByRestaurantId(Integer restaurantId);
}