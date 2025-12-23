package com.s2o.app.repository;

import com.s2o.app.entity.Order; // Giả sử bạn đã map entity Order
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface ManagerDashboardRepository extends JpaRepository<Order, Integer> {

    // 1. Tính tổng doanh thu trong khoảng thời gian (cho ngày hôm nay)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.restaurantId = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate AND o.status = 'COMPLETED'")
    BigDecimal sumRevenueByDateRange(@Param("restaurantId") Integer restaurantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 2. Đếm số đơn hàng đang phục vụ (Pending/Confirmed/Served)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurantId = :restaurantId AND o.status IN ('PENDING', 'CONFIRMED', 'SERVED')")
    long countActiveOrders(@Param("restaurantId") Integer restaurantId);

    // 3. Đếm số đơn hàng hoàn thành hôm nay
    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurantId = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate AND o.status = 'COMPLETED'")
    int countCompletedOrdersToday(@Param("restaurantId") Integer restaurantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}