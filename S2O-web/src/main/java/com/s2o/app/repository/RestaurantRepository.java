package com.s2o.app.repository;

import com.s2o.app.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // JpaRepository đã có sẵn các hàm save, findById
}