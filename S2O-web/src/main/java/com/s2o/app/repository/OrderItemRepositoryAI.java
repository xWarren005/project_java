// sử dụng để thống kê món bán chạy nhất cho AI
package com.s2o.app.repository;

import com.s2o.app.entity.OrderItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepositoryAI extends JpaRepository<OrderItem, Long> {

    @Query("""
   SELECT oi.product.id, SUM(oi.quantity) 
   FROM OrderItem oi 
   WHERE oi.order.restaurantId = :rid
   GROUP BY oi.product.id
   ORDER BY SUM(oi.quantity) DESC
""")
    List<Object[]> findTopProducts(@Param("rid") Integer restaurantId);
}
