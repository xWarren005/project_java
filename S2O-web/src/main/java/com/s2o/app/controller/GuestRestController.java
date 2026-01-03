package com.s2o.app.controller;

import com.s2o.app.dto.GuestOrderRequest;
import com.s2o.app.repository.ProductRepository;
import com.s2o.app.service.GuestMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/guest")
public class GuestRestController {

    @Autowired private GuestMenuService guestService;
    @Autowired private ProductRepository productRepository;
    @Autowired private GuestMenuService guestMenuService;

    // 1. API Lấy Menu (Cho khách xem)
    @GetMapping("/menu")
    public ResponseEntity<?> getPublicMenu(@RequestParam(defaultValue = "1") Integer restaurantId) {
        // Gọi hàm logic vừa tạo
        return ResponseEntity.ok(guestMenuService.getGuestMenuData(restaurantId));
    }

    // API đặt món
    @PostMapping("/order")
    public ResponseEntity<?> placeGuestOrder(@RequestBody GuestOrderRequest request) {
        try {
            guestMenuService.createGuestOrder(request);
            return ResponseEntity.ok("Đặt món thành công! Vui lòng đợi xác nhận.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}