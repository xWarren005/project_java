package com.s2o.app.service;
import com.s2o.app.entity.Order;
import com.s2o.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class UserPaymentService {
    @Autowired
    private OrderRepository orderRepository;
    @Transactional
    public void processPayment(Integer userId, String paymentMethod) {
        // 1. Tìm tất cả các đơn chưa thanh toán của User này
        List<String> activeStatuses = Arrays.asList("PENDING", "COOKING", "READY", "COMPLETED");

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getUserId() != null && o.getUserId().equals(userId))
                .filter(o -> activeStatuses.contains(o.getStatus()))
                .toList();

        if (orders.isEmpty()) {
            // Kiểm tra xem có đơn nào đang chờ thanh toán không (tránh spam nút)
            boolean hasPendingPayment = orderRepository.findAll().stream()
                    .anyMatch(o -> o.getUserId().equals(userId) && "PAYMENT_PENDING".equals(o.getStatus()));

            if (hasPendingPayment) {
                throw new RuntimeException("Bạn đã gửi yêu cầu rồi, vui lòng đợi thu ngân.");
            }
            throw new RuntimeException("Không có đơn hàng nào để thanh toán");
        }
        // 2. CHUYỂN TRẠNG THÁI SANG: PAYMENT_PENDING
        for (Order order : orders) {
            order.setStatus("PAYMENT_PENDING");
            // Lưu paymentMethod nếu cần
        }

        orderRepository.saveAll(orders);
    }
}
