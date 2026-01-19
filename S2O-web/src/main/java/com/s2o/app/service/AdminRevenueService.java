package com.s2o.app.service;

import com.s2o.app.dto.RestaurantRevenueStat;
import com.s2o.app.dto.RevenueDTO;
import com.s2o.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId; // Import quan trọng
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminRevenueService {

    @Autowired
    private OrderRepository orderRepository;

    public RevenueDTO getRevenueData() {
        // 1. 🔥 CHỐT MÚI GIỜ VIỆT NAM (Quan trọng)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime startOfDay = today.atStartOfDay();      // 2026-01-20 00:00:00
        LocalDateTime endOfDay = today.atTime(23, 59, 59);    // 2026-01-20 23:59:59

        // 2. Gọi hàm mới sửa trong Repo, truyền thời gian vào
        BigDecimal bdToday = orderRepository.sumGmvByRange(startOfDay, endOfDay);

        // Các hàm tháng/năm giữ nguyên (hoặc bạn có thể viết logic tương tự nếu muốn chính xác tuyệt đối)
        BigDecimal bdMonth = orderRepository.sumGmvCurrentMonth();
        BigDecimal bdYear = orderRepository.sumGmvCurrentYear();

        // 3. Ép kiểu an toàn
        Double gmvToday = bdToday != null ? bdToday.doubleValue() : 0.0;
        Double gmvMonth = bdMonth != null ? bdMonth.doubleValue() : 0.0;
        Double gmvYear = bdYear != null ? bdYear.doubleValue() : 0.0;

        // 4. Lấy danh sách chi tiết
        List<Object[]> rawList = orderRepository.getMonthlyRevenueByRestaurant();
        List<RestaurantRevenueStat> stats = new ArrayList<>();

        for (Object[] row : rawList) {
            String resName = (String) row[0];
            BigDecimal bdTotal = (BigDecimal) row[1];
            Double revenue = bdTotal != null ? bdTotal.doubleValue() : 0.0;
            stats.add(new RestaurantRevenueStat(resName, revenue));
        }

        return new RevenueDTO(gmvToday, gmvMonth, gmvYear, stats);
    }
}