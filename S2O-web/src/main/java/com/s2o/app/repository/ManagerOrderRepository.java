package com.s2o.app.repository;

import com.s2o.app.entity.Order;
import com.s2o.app.dto.response.TopDishDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ManagerOrderRepository extends JpaRepository<Order, Integer> {

    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.restaurantId = :rid
        AND o.status IN ('PENDING','CONFIRMED','SERVED')
    """)
    long countActiveOrders(@Param("rid") Integer restaurantId);

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount),0)
        FROM Order o
        WHERE o.restaurantId = :rid
        AND o.status = 'COMPLETED'
        AND o.createdAt BETWEEN :start AND :end
    """)
    BigDecimal sumCompletedRevenue(
            @Param("rid") Integer restaurantId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.restaurantId = :rid
        AND o.status = 'COMPLETED'
        AND o.createdAt BETWEEN :start AND :end
    """)
    Integer countCompletedOrdersToday(
            @Param("rid") Integer restaurantId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COUNT(DISTINCT o.userId)
        FROM Order o
        WHERE o.restaurantId = :rid
        AND o.status = 'COMPLETED'
        AND o.createdAt BETWEEN :start AND :end
    """)
    Long countCustomers(
            @Param("rid") Integer restaurantId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """
        SELECT DATE_FORMAT(created_at,'%d/%m') AS day,
               SUM(total_amount) AS revenue
        FROM orders
        WHERE restaurant_id = :rid
        AND status = 'COMPLETED'
        AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        GROUP BY restaurant_id, DATE(created_at)
        ORDER BY DATE(created_at)
    """, nativeQuery = true)
    List<Object[]> getRevenueLast7Days(@Param("rid") Integer restaurantId);

    @Query("""
        SELECT new com.s2o.app.dto.response.TopDishDTO(
            p.id,
            p.name,
            SUM(oi.quantity),
            SUM(oi.quantity * oi.unitPrice),
            p.imageUrl
        )
        FROM OrderItem oi
        JOIN oi.order o
        JOIN oi.product p
        WHERE o.restaurantId = :rid
        AND o.status = 'COMPLETED'
        GROUP BY p.id, p.name, p.imageUrl
        ORDER BY SUM(oi.quantity * oi.unitPrice) DESC
    """)
    List<TopDishDTO> getTopSellingDishes(@Param("rid") Integer restaurantId);
}
