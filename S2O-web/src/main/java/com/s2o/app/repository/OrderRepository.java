package com.s2o.app.repository;

import com.s2o.app.entity.Order;
import com.s2o.app.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.restaurant " +
            "WHERE o.userId = :userId " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

    // --- CHEF DASHBOARD ---
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.restaurantTable " +
            "WHERE o.restaurantId = :restaurantId " +
            "AND o.status IN (:statuses) " +
            "ORDER BY o.createdAt ASC")
    List<Order> findChefOrders(
            @Param("restaurantId") Integer restaurantId,
            @Param("statuses") List<String> statuses
    );

    // --- GIỮ NGUYÊN ---
    long countByRestaurantIdAndStatus(Integer restaurantId, String status);


    Optional<Order> findFirstByTableIdAndStatusInOrderByCreatedAtDesc(
            Integer tableId,
            List<OrderStatus> statuses
    );

    @Query("SELECT o FROM Order o " +
            "WHERE o.restaurantId = :restaurantId " +
            "ORDER BY o.createdAt DESC")
    List<Order> findOrdersByRestaurantId(@Param("restaurantId") Integer restaurantId);
    List<Order> findTop10ByOrderByCreatedAtDesc();
    List<Order> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'PAID'")
    Double sumTotalRevenue();

    // 2. [QUAN TRỌNG] Dùng nativeQuery = true để chạy SQL thuần, tránh lỗi Hibernate
    @Query(value = "SELECT MONTH(created_at) as month, SUM(total_amount) as total " +
            "FROM orders " +
            "WHERE status = 'PAID' AND YEAR(created_at) = YEAR(CURRENT_DATE()) " +
            "GROUP BY MONTH(created_at) " +
            "ORDER BY MONTH(created_at)", nativeQuery = true)
    List<Object[]> getMonthlyRevenue();
}
