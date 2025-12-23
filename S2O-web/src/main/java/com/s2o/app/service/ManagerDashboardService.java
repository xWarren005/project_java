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
    @Autowired private TableRepository tableRepo; // Cần tạo repository này
    @Autowired private ProductRepository productRepo; // Cần tạo repository này

    public ManagerOverviewResponse getDashboardData(Integer restaurantId) {
        ManagerOverviewResponse response = new ManagerOverviewResponse();

        // --- 1. Lấy dữ liệu Tables ---
        List<RestaurantTable> tables = tableRepo.findByRestaurantId(restaurantId);

        // Map entity sang DTO và chuyển đổi status ENUM sang số (0, 1, 2)
        List<TableDTO> tableDTOs = tables.stream().map(t -> {
            int statusInt = 0; // AVAILABLE
            if ("OCCUPIED".equals(t.getStatus())) statusInt = 1;
            else if ("RESERVED".equals(t.getStatus())) statusInt = 2;

            return new TableDTO(t.getId(), statusInt, t.getTableName());
        }).collect(Collectors.toList());
        response.setTables(tableDTOs);

        // --- 2. Tính toán Stats ---
        long totalTables = tables.size();
        long occupiedTables = tables.stream().filter(t -> "OCCUPIED".equals(t.getStatus())).count();
        long totalDishes = productRepo.countByRestaurantId(restaurantId); // Giả sử có hàm này
        long activeOrders = orderRepo.countActiveOrders(restaurantId);

        // Tính doanh thu hôm nay
        LocalDateTime startToday = LocalDate.now().atStartOfDay();
        LocalDateTime endToday = LocalDate.now().atTime(23, 59, 59);
        BigDecimal revenueToday = orderRepo.sumRevenueByDateRange(restaurantId, startToday, endToday);

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
        revDetail.setGrowth(0.0); // Cần logic tính so với hôm qua
        revDetail.setOrdersCount(orderRepo.countCompletedOrdersToday(restaurantId, startToday, endToday));
        revDetail.setInvoicesCount(0); // Cần query bảng Invoice
        response.setRevenueDetail(revDetail);

        return response;
    }

    private String convertToCurrency(BigDecimal value) {
        return value == null ? "0 VNĐ" : String.format("%,.0f VNĐ", value);
    }
}