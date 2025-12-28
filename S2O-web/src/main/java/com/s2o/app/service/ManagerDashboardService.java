package com.s2o.app.service;

import com.s2o.app.dto.response.*;
import com.s2o.app.entity.RestaurantTable;
import com.s2o.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerDashboardService {

    @Autowired private ManagerOrderRepository orderRepo;
    @Autowired private TableRepository tableRepo;
    @Autowired private ProductRepository productRepo;

    // =========================================================
    // PHẦN 1: Dashboard Overview
    // =========================================================
    public ManagerOverviewResponse getDashboardData(Integer restaurantId) {
        ManagerOverviewResponse response = new ManagerOverviewResponse();

        List<RestaurantTable> tables = tableRepo.findByRestaurantId(restaurantId);
        response.setTables(
                tables.stream().map(TableDTO::fromEntity).collect(Collectors.toList())
        );

        long totalTables = tables.size();
        long occupiedTables = tables.stream()
                .filter(t -> t.getStatus() == RestaurantTable.TableStatus.OCCUPIED)
                .count();

        long totalDishes = productRepo.countByRestaurantId(restaurantId);
        long activeOrders = orderRepo.countActiveOrders(restaurantId);

        LocalDateTime startToday = LocalDate.now().atStartOfDay();
        LocalDateTime endToday = LocalDate.now().atTime(23, 59, 59);

        // ✅ SỬA: chỉ tính COMPLETED cho đúng DB
        BigDecimal revenueToday = orderRepo.sumCompletedRevenue(restaurantId, startToday, endToday);
        if (revenueToday == null) revenueToday = BigDecimal.ZERO;

        StatsGroupDTO stats = new StatsGroupDTO();
        stats.setTotalTables(new StatsDTO(String.valueOf(totalTables), occupiedTables + " bàn đang có khách"));
        stats.setDishes(new StatsDTO(String.valueOf(totalDishes), "Đang phục vụ"));
        stats.setOrders(new StatsDTO(String.valueOf(activeOrders), "Đơn đang xử lý"));
        stats.setRevenueToday(new StatsDTO(convertToCurrency(revenueToday), "Hôm nay"));
        response.setStats(stats);

        RevenueDetailDTO revDetail = new RevenueDetailDTO();
        revDetail.setTotal(convertToCurrency(revenueToday));
        revDetail.setGrowth(0.0);

        Integer ordersCount = orderRepo.countCompletedOrdersToday(restaurantId, startToday, endToday);
        revDetail.setOrdersCount(ordersCount != null ? ordersCount : 0);
        revDetail.setInvoicesCount(0);
        response.setRevenueDetail(revDetail);

        return response;
    }

    private String convertToCurrency(BigDecimal value) {
        return value == null ? "0 VNĐ" : String.format("%,.0f VNĐ", value);
    }

    // =========================================================
    // PHẦN 2: Revenue Report
    // =========================================================
    public RevenueDashboardResponse getRevenueStats(Integer restaurantId) {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        BigDecimal revenueToday = orderRepo.sumCompletedRevenue(restaurantId, startOfDay, endOfDay);

        Integer ordersToday = orderRepo.countCompletedOrdersToday(restaurantId, startOfDay, endOfDay);
        if (ordersToday == null) ordersToday = 0;

        Long customersToday = orderRepo.countCustomers(restaurantId, startOfDay, endOfDay);

        List<TopDishDTO> topDishes = orderRepo.getTopSellingDishes(restaurantId);

        // ✅ SỬA constructor
        TopDishDTO bestSeller = topDishes.isEmpty()
                ? new TopDishDTO(0, "Chưa có", 0L, 0, "")
                : topDishes.get(0);

        List<SummaryStatDTO> summary = new ArrayList<>();
        summary.add(new SummaryStatDTO("Doanh Thu Hôm Nay", formatShortCurrency(revenueToday), "Hôm nay", "fa-dollar-sign"));
        summary.add(new SummaryStatDTO("Đơn Hàng", ordersToday.toString(), "Đã hoàn thành", "fa-cart-shopping"));
        summary.add(new SummaryStatDTO("Món Bán Chạy", bestSeller.getName(), bestSeller.getCount() + " phần", "fa-fire"));
        summary.add(new SummaryStatDTO(
                "Khách Hàng",
                String.valueOf(customersToday == null ? 0 : customersToday),
                "Khách mua hàng",
                "fa-users"
        ));

        List<ChartDataDTO> chartData = orderRepo.getRevenueLast7Days(restaurantId)
                .stream()
                .map(obj -> new ChartDataDTO(
                        (String) obj[0],
                        ((Number) obj[1]).doubleValue()
                ))
                .collect(Collectors.toList());

        List<TopDishDTO> top5Dishes = topDishes.stream().limit(5).collect(Collectors.toList());

        return new RevenueDashboardResponse(summary, chartData, top5Dishes);
    }

    private String formatShortCurrency(BigDecimal amount) {
        if (amount == null) return "0đ";
        double val = amount.doubleValue();
        if (val >= 1_000_000) return String.format("%.1ftr", val / 1_000_000);
        if (val >= 1_000) return String.format("%.0fk", val / 1_000);
        return String.format("%.0fđ", val);
    }
}
