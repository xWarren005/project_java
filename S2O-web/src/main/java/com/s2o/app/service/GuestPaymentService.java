package com.s2o.app.service;

import com.s2o.app.entity.Order;
import com.s2o.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestPaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void processGuestPayment(Integer tableId, String paymentMethod) {
        // 1. Danh sách các trạng thái "Đang phục vụ" cần tính tiền
        List<String> activeStatuses = Arrays.asList("PENDING", "COOKING", "READY", "COMPLETED");

        // 2. Tìm các đơn hàng của BÀN NÀY (Thay vì userId)
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getTableId() != null && o.getTableId().equals(tableId)) // <--- SỬA THÀNH TABLE ID
                .filter(o -> activeStatuses.contains(o.getStatus()))
                .collect(Collectors.toList());

        // 3. Xử lý trường hợp không tìm thấy đơn nào
        if (orders.isEmpty()) {
            // Kiểm tra xem bàn này đã gửi yêu cầu trước đó chưa (Trạng thái PAYMENT_PENDING)
            boolean hasPendingPayment = orderRepository.findAll().stream()
                    .anyMatch(o -> o.getTableId() != null
                            && o.getTableId().equals(tableId)
                            && "PAYMENT_PENDING".equals(o.getStatus()));

            if (hasPendingPayment) {
                throw new RuntimeException("Bàn đang chờ thu ngân xử lý, vui lòng đợi.");
            }
            throw new RuntimeException("Bàn này không có món nào cần thanh toán.");
        }

        // 4. CHUYỂN TRẠNG THÁI SANG: PAYMENT_PENDING (Chờ thu ngân)
        for (Order order : orders) {
            order.setStatus("PAYMENT_PENDING");
            // Nếu Entity Order có trường paymentMethod, bạn có thể lưu vào đây
            // order.setPaymentMethod(paymentMethod);
        }

        orderRepository.saveAll(orders);
    }
}