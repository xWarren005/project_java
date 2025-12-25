package com.s2o.app.service;

import com.s2o.app.dto.response.*;
import com.s2o.app.entity.RestaurantTable;
import com.s2o.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerDashboardService {

    @Autowired private ManagerDashboardRepository orderRepo;
    @Autowired private TableRepository tableRepo;
    @Autowired private ProductRepository productRepo;

    public ManagerOverviewResponse getDashboardData(Integer restaurantId) {
        ManagerOverviewResponse response = new ManagerOverviewResponse();

        // --- 1. Lấy dữ liệu Tables ---
        List<RestaurantTable> tables = tableRepo.findByRestaurantId(restaurantId);

        // SỬA LỖI 1: Dùng hàm static fromEntity thay vì new TableDTO thủ công
        // Hàm này đã xử lý việc map status Enum sang số 0,1,2 bên trong TableDTO.java rồi
        List<TableDTO> tableDTOs = tables.stream()
                .map(TableDTO::fromEntity)
                .collect(Collectors.toList());
        response.setTables(tableDTOs);

        // --- 2. Tính toán Stats ---
        long totalTables = tables.size();

        // SỬA LỖI 2: So sánh Enum trực tiếp, không dùng String equals
        long occupiedTables = tables.stream()
                .filter(t -> t.getStatus() == RestaurantTable.TableStatus.OCCUPIED)
                .count();

        long totalDishes = productRepo.countByRestaurantId(restaurantId);
        long activeOrders = orderRepo.countActiveOrders(restaurantId);

        // Tính doanh thu hôm nay
        LocalDateTime startToday = LocalDate.now().atStartOfDay();
        LocalDateTime endToday = LocalDate.now().atTime(23, 59, 59);
        BigDecimal revenueToday = orderRepo.sumRevenueByDateRange(restaurantId, startToday, endToday);
        if (revenueToday == null) revenueToday = BigDecimal.ZERO; // Tránh lỗi NullPointerException

        // Tạo StatsGroup
        StatsGroupDTO stats = new StatsGroupDTO();
        stats.setTotalTables(new StatsDTO(String.valueOf(totalTables), occupiedTables + " bàn đang có khách"));
        stats.setDishes(new StatsDTO(String.valueOf(totalDishes), "Đang phục vụ"));
        stats.setOrders(new StatsDTO(String.valueOf(activeOrders), "Đơn đang xử lý"));
        stats.setRevenueToday(new StatsDTO(convertToCurrency(revenueToday), "Hôm nay"));
        response.setStats(stats);

        // --- 3. Revenue Detail ---
        RevenueDetailDTO revDetail = new RevenueDetailDTO();
        revDetail.setTotal(convertToCurrency(revenueToday));
        revDetail.setGrowth(0.0); // Logic tính tăng trưởng so với hôm qua (để sau)

        // Kiểm tra null cho count
        Integer ordersCount = orderRepo.countCompletedOrdersToday(restaurantId, startToday, endToday);
        revDetail.setOrdersCount(ordersCount != null ? ordersCount : 0);

        revDetail.setInvoicesCount(0); // Tạm thời để 0
        response.setRevenueDetail(revDetail);

        return response;
    }

    private String convertToCurrency(BigDecimal value) {
        return value == null ? "0 VNĐ" : String.format("%,.0f VNĐ", value);
    }
}