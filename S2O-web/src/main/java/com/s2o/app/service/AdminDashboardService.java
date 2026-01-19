package com.s2o.app.service;

import com.s2o.app.dto.ActivityLogDTO;
import com.s2o.app.dto.RestaurantDTO;
import com.s2o.app.dto.StatItem;
import com.s2o.app.entity.Order;
import com.s2o.app.entity.Restaurant;
import com.s2o.app.repository.OrderRepository;
import com.s2o.app.repository.RestaurantRepository;
import com.s2o.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    // 1. STATS CARDS (THẬT)
    public List<StatItem> getStats() {
        long activeRestaurants = restaurantRepository.countByApprovalStatus("APPROVED");

        // Đếm User
        long totalUsers = userRepository.count();

        // Tổng doanh thu
        Double revenue = orderRepository.sumTotalRevenue();
        if (revenue == null) { revenue = 0.0; } // Xử lý null

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String revenueStr = nf.format(revenue);

        return Arrays.asList(
                new StatItem("Nhà hàng (Chờ duyệt)", String.valueOf(activeRestaurants), "", true, "store", "icon-blue"),
                new StatItem("Tổng Người dùng", String.valueOf(totalUsers), "", true, "users", "icon-purple"),
                new StatItem("Tổng Doanh thu", revenueStr, "", true, "dollar-sign", "icon-green"),
                new StatItem("Hệ thống", "Online", "", true, "activity", "icon-pink")
        );
    }

    // 2. BIỂU ĐỒ DOANH THU (THẬT)
    public Map<String, Object> getRevenueChartData() {
        List<Object[]> rawData = orderRepository.getMonthlyRevenue();

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        Map<Integer, Double> revenueMap = new HashMap<>();

        // [QUAN TRỌNG]: SỬA LỖI ÉP KIỂU GÂY TREO API
        // Dùng ((Number) val).doubleValue() để chấp nhận cả BigDecimal và Integer
        for (Object[] row : rawData) {
            if (row[0] != null && row[1] != null) {
                try {
                    Integer month = ((Number) row[0]).intValue();
                    Double total = ((Number) row[1]).doubleValue();
                    revenueMap.put(month, total);
                } catch (Exception e) {
                    System.err.println("Lỗi convert data biểu đồ: " + e.getMessage());
                }
            }
        }

        // Tạo dữ liệu cho 6 tháng đầu năm
        for (int i = 1; i <= 6; i++) {
            labels.add("Tháng " + i);
            data.add(revenueMap.getOrDefault(i, 0.0));
        }

        Map<String, Object> chartResult = new HashMap<>();
        chartResult.put("labels", labels);
        chartResult.put("data", data);
        return chartResult;
    }

    // 3. NHÀ HÀNG MỚI (THẬT)
    public List<RestaurantDTO> getNewRestaurants() {
        List<Restaurant> list = restaurantRepository.findAll();
        // Sort trong Java để tránh lỗi DB nếu chưa index
        list.sort((r1, r2) -> r2.getId().compareTo(r1.getId()));

        return list.stream().limit(5).map(r -> new RestaurantDTO(
                r.getId(),
                r.getName(),
                r.getAddress(),
                r.getApprovalStatus() != null ? r.getApprovalStatus() : "UNKNOWN",
                r.getRating() != null ? r.getRating() : 0.0
        )).collect(Collectors.toList());
    }

    // 4. HOẠT ĐỘNG GẦN ĐÂY
    public List<ActivityLogDTO> getRecentActivities() {
        List<Order> orders = orderRepository.findTop5ByOrderByCreatedAtDesc();

        return orders.stream().map(o -> {
            String time = o.getCreatedAt() != null ?
                    o.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "-";

            String statusColor = "success";
            if("PENDING".equals(o.getStatus())) statusColor = "warning";
            if("CANCELLED".equals(o.getStatus())) statusColor = "error";

            // Format tiền
            String totalStr = "0 đ";
            if(o.getTotalAmount() != null) {
                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                totalStr = nf.format(o.getTotalAmount());
            }

            return new ActivityLogDTO(
                    time,
                    "Đơn #" + o.getId(),
                    "Đặt món",
                    "Tổng: " + totalStr,
                    o.getStatus(),
                    statusColor
            );
        }).collect(Collectors.toList());
    }
}