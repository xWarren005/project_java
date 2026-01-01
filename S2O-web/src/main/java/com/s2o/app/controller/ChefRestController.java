package com.s2o.app.controller;

import com.s2o.app.dto.response.ChefDashboardResponse;
import com.s2o.app.service.ChefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chef")
public class ChefRestController {

    @Autowired
    private ChefService chefService;

    // API lấy dữ liệu dashboard
    // GET: /api/chef/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<ChefDashboardResponse> getDashboard() {
        // TODO: Khi tích hợp Spring Security, hãy lấy ID nhà hàng từ User đang đăng nhập.
        // Ví dụ: User user = authService.getCurrentUser(); Integer restaurantId = user.getRestaurantId();

        Integer currentRestaurantId = 1; // Hardcode để test

        return ResponseEntity.ok(chefService.getDashboardData(currentRestaurantId));
    }

    // API cập nhật trạng thái đơn hàng
    // POST: /api/chef/order/{id}/status
    // Body: { "status": "COOKING" }
    @PostMapping("/order/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload
    ) {
        String status = payload.get("status");
        // Frontend cần gửi đúng chuỗi: "PENDING", "COOKING", "READY", "COMPLETED"
        chefService.updateOrderStatus(id, status);
        return ResponseEntity.ok(Map.of("message", "Cập nhật thành công"));
    }
}