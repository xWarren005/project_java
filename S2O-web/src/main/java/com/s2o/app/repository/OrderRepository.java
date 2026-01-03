package com.s2o.app.repository;

import com.s2o.app.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // --- Code Cũ (Giữ nguyên) ---
    // Tìm đơn hàng theo User ID, sắp xếp mới nhất lên đầu
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.restaurant " +  // <-- Lấy luôn thông tin Nhà hàng
            "WHERE o.userId = :userId " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

    // ========================================================================
    // CODE MỚI THÊM CHO CHEF DASHBOARD
    // ========================================================================

    /**
     * Lấy danh sách đơn hàng cho Bếp dựa trên Status và RestaurantID.
     * Sử dụng JOIN FETCH để lấy luôn thông tin bàn (RestaurantTable) tránh lỗi LazyLoading.
     * Vì status là String nên truyền List<String> vào (VD: ["PENDING", "COOKING", "READY"])
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.restaurantTable " +
            "WHERE o.restaurantId = :restaurantId " +
            "AND o.status IN (:statuses) " +
            "ORDER BY o.createdAt ASC")
    List<Order> findChefOrders(
            @Param("restaurantId") Integer restaurantId,
            @Param("statuses") List<String> statuses
    );

    /**
     * Đếm số lượng đơn hàng theo trạng thái để hiển thị lên dashboard (VD: 5 đang chờ, 2 đang nấu)
     */
    long countByRestaurantIdAndStatus(Integer restaurantId, String status);

    // Tìm đơn hàng mới nhất của bàn mà chưa hoàn thành (để tính là bàn đang BUSY)
    // Giả sử trạng thái active là PENDING, COOKING, READY, COMPLETED (chưa thanh toán)
    @Query("SELECT o FROM Order o " +
            "WHERE o.tableId = :tableId " +
            "AND o.status IN ('PENDING', 'COOKING', 'READY', 'COMPLETED') " +
            "ORDER BY o.createdAt DESC LIMIT 1")
    Optional<Order> findActiveOrderByTableId(@Param("tableId") Integer tableId);
}