package com.s2o.app.service;

import com.s2o.app.dto.RevenueDTO;
import com.s2o.app.dto.RestaurantRevenueStat;
import com.s2o.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminRevenueService {

    @Autowired
    private OrderRepository orderRepository;

    public RevenueDTO getRevenueData() {
        // 1. Lấy dữ liệu dạng BigDecimal từ DB
        BigDecimal bdToday = orderRepository.sumGmvToday();
        BigDecimal bdMonth = orderRepository.sumGmvCurrentMonth();
        BigDecimal bdYear = orderRepository.sumGmvCurrentYear();

        // 2. Chuyển sang Double để tính toán
        Double gmvToday = bdToday != null ? bdToday.doubleValue() : 0.0;
        Double gmvMonth = bdMonth != null ? bdMonth.doubleValue() : 0.0;
        Double gmvYear = bdYear != null ? bdYear.doubleValue() : 0.0;


        // 4. Lấy chi tiết từng nhà hàng
        List<Object[]> rawList = orderRepository.getMonthlyRevenueByRestaurant();
        List<RestaurantRevenueStat> stats = new ArrayList<>();

        for (Object[] row : rawList) {
            String resName = (String) row[0];

            // [QUAN TRỌNG]: SUM trong DB trả về BigDecimal, cần ép kiểu đúng
            BigDecimal bdTotal = (BigDecimal) row[1];
            Double totalRevenue = bdTotal != null ? bdTotal.doubleValue() : 0.0;

            stats.add(new RestaurantRevenueStat(resName, totalRevenue));
        }

        return new RevenueDTO(gmvToday, gmvMonth, gmvYear, stats);
    }
}