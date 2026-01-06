package com.s2o.app.service;

import com.s2o.app.dto.TransactionDTO;
import com.s2o.app.entity.Order;
import com.s2o.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminRevenueService {

    @Autowired
    private OrderRepository orderRepository;

    public List<TransactionDTO> getAllTransactions() {
        // Lấy 10 đơn hàng mới nhất
        List<Order> orders = orderRepository.findTop10ByOrderByCreatedAtDesc();

        return orders.stream().map(o -> {
            String resName = "Unknown";
            // Code đồng đội dùng FetchType.LAZY, cần cẩn thận null pointer
            if (o.getRestaurant() != null) {
                resName = o.getRestaurant().getName();
            }

            return new TransactionDTO(
                    o.getId().longValue(), // Order ID Integer -> Long
                    o.getCreatedAt() != null ? o.getCreatedAt().toString() : "N/A",
                    resName,
                    "Standard",
                    o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0.0,
                    o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() * 0.1 : 0.0
            );
        }).collect(Collectors.toList());
    }

    // Các hàm thống kê tạm giữ nguyên hoặc trả về String tĩnh
    public String getTodayRevenue() { return "$0"; }
    public String getMonthRevenue() { return "$0"; }
    public String getPredictedRevenue() { return "$0"; }
}