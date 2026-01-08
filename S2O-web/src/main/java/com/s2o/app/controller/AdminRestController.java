package com.s2o.app.controller;

import com.s2o.app.dto.RestaurantDTO;
import com.s2o.app.dto.UserDTO;
import com.s2o.app.service.AdminDashboardService;
import com.s2o.app.service.AdminRestaurantService;
import com.s2o.app.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    @Autowired
    private AdminDashboardService dashboardService;

    @Autowired
    private AdminRestaurantService restaurantService;
    @Autowired
    private AdminUserService adminUserService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        Map<String, Object> response = new HashMap<>();

        // 1. Stats
        response.put("stats", dashboardService.getStats());

        // 2. Chart (Labels + Data thật)
        response.put("chartData", dashboardService.getRevenueChartData());

        // 3. List Nhà hàng mới
        response.put("newRestaurants", dashboardService.getNewRestaurants());

        // 4. Hoạt động (Đơn hàng mới)
        response.put("activities", dashboardService.getRecentActivities());

        return ResponseEntity.ok(response);
    }
    // 1. Lấy danh sách & Thống kê
    @GetMapping("/restaurants")
    public ResponseEntity<?> getAllRestaurants() {
        Map<String, Object> data = new HashMap<>();

        // Danh sách nhà hàng
        data.put("list", restaurantService.getAllRestaurants());

        // Số liệu thống kê trên đầu trang
        data.put("total", restaurantService.getTotalCount());
        data.put("pending", restaurantService.getPendingCount());
        data.put("rating", restaurantService.getAvgRating());

        return ResponseEntity.ok(data);
    }

    // 2. Thêm nhà hàng mới
    @PostMapping("/restaurants")
    public ResponseEntity<?> createRestaurant(@RequestBody RestaurantDTO dto) {
        restaurantService.createRestaurant(dto);
        return ResponseEntity.ok("Thêm thành công ");
    }

    // 3. Duyệt nhà hàng
    @PutMapping("/restaurants/{id}/approve")
    public ResponseEntity<?> approveRestaurant(@PathVariable Long id) {
        restaurantService.approveRestaurant(id); // Cần bổ sung hàm này vào Service
        return ResponseEntity.ok("Đã duyệt nhà hàng");
    }

    // 4. Xóa nhà hàng
    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id); // Cần bổ sung hàm này vào Service
        return ResponseEntity.ok("Đã xóa nhà hàng");
    }
    // 5. Cập nhật thông tin nhà hàng (Tên, Địa chỉ...)
    @PutMapping("/restaurants/{id}")
    public ResponseEntity<?> updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO dto) {
        restaurantService.updateRestaurant(id, dto);
        return ResponseEntity.ok("Cập nhật thành công");
    }
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        Map<String, Object> data = new HashMap<>();
        data.put("list", adminUserService.getAllUsers());
        data.put("total", adminUserService.getTotalUsers());
        return ResponseEntity.ok(data);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDTO dto) {
        try {
            adminUserService.createUser(dto);
            return ResponseEntity.ok("Thêm thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserDTO dto) {
        adminUserService.updateUser(id, dto);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok("Xóa thành công");
    }
}