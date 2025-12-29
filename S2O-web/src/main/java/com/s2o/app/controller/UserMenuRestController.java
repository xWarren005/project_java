package com.s2o.app.controller;

import com.s2o.app.dto.UserOrderRequest;
import com.s2o.app.entity.User;
import com.s2o.app.service.UserMenuService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserMenuRestController {

    @Autowired
    private UserMenuService userMenuService;

    // 1. API Lấy Menu (GET)
    @GetMapping("/menu-data")
    public ResponseEntity<?> getMenuData(HttpSession session) {
        // Lấy ID nhà hàng từ Session
        Integer restaurantId = (Integer) session.getAttribute("RESTAURANT_ID");

        // Fallback cho môi trường test nếu không quét QR
        if (restaurantId == null) restaurantId = 1;

        return ResponseEntity.ok(userMenuService.getMenuData(restaurantId));
    }

    // 2. API Đặt món (POST)
    @PostMapping("/menu/order")
    public ResponseEntity<?> placeOrder(@RequestBody UserOrderRequest request, HttpSession session) {
        // 1. Kiểm tra đăng nhập
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập để gọi món");
        }

        try {
            // 2. Gọi Service xử lý lưu DB
            userMenuService.createOrder(user, request);
            return ResponseEntity.ok("Đặt món thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi xử lý đơn hàng: " + e.getMessage());
        }
    }
}