package com.s2o.app.controller;
import com.s2o.app.entity.User;
import com.s2o.app.service.UserPaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/payment")
public class UserPaymentRestController {
    @Autowired
    private UserPaymentService UserpaymentService;
    // API: Thực hiện thanh toán
    // POST /api/user/payment/checkout
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, String> payload, HttpSession session) {
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");

        String method = payload.get("method"); // "CASH" hoặc "TRANSFER"

        try {
            UserpaymentService.processPayment(user.getId(), method);
            return ResponseEntity.ok("Thanh toán thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
