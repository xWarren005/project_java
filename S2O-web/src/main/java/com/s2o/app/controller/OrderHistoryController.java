package com.s2o.app.controller;
import com.s2o.app.dto.response.OrderHistoryResponse;
import com.s2o.app.entity.User;
import com.s2o.app.service.OrderHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/orders") // URL gốc riêng cho Orders
public class OrderHistoryController {
    @Autowired
    private OrderHistoryService orderHistoryService;

    // API: Lấy lịch sử đơn hàng
    // URL: GET /api/user/orders/history
    @GetMapping("/orders")
    public ResponseEntity<?> getMyOrders(HttpSession session) {
        // 1. Kiểm tra User từ Session
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập");
        return ResponseEntity.ok(orderHistoryService.getUserOrderHistory(user.getId()));
    }
    @GetMapping("/history")
    public ResponseEntity<?> getPaidInvoices(HttpSession session) {
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập");

        return ResponseEntity.ok(orderHistoryService.getPaidOrders(user.getId()));
    }
}
