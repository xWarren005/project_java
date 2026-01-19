package com.s2o.app.repository;

import com.s2o.app.entity.Order;
import com.s2o.app.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    // 1. Tổng doanh thu HÔM NAY (Tính cả PAID và COMPLETED)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE (o.status = 'PAID' OR o.status = 'COMPLETED') " +
            "AND o.createdAt BETWEEN :start AND :end")
    BigDecimal sumGmvByRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 2. Tổng doanh thu THÁNG NAY
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE (o.status = 'PAID' OR o.status = 'COMPLETED') " +
            "AND MONTH(o.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE)")
    BigDecimal sumGmvCurrentMonth();

    // 3. Tổng doanh thu NĂM NAY
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE (o.status = 'PAID' OR o.status = 'COMPLETED') " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE)")
    BigDecimal sumGmvCurrentYear();

    // 4. Doanh thu theo từng nhà hàng (Tháng này)
    @Query("SELECT o.restaurant.name, SUM(o.totalAmount) " +
            "FROM Order o " +
            "WHERE (o.status = 'PAID' OR o.status = 'COMPLETED') " +
            "AND MONTH(o.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) " +
            "GROUP BY o.restaurant.id, o.restaurant.name " +
            "ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> getMonthlyRevenueByRestaurant();
}