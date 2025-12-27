package com.s2o.app.repository;

import com.s2o.app.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Tìm đơn hàng theo User ID, sắp xếp mới nhất lên đầu
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
