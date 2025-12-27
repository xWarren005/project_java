package com.s2o.app.controller;

import com.s2o.app.service.DashboardService;
import com.s2o.app.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // --- 1. INJECT CÁC SERVICE XỬ LÝ DỮ LIỆU ---
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private RestaurantService restaurantService;

    // =========================================================
    // 2. TRANG DASHBOARD TỔNG QUAN
    // =========================================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Lấy dữ liệu thống kê từ DashboardService
        model.addAttribute("stats", dashboardService.getStats());
        // Lấy dữ liệu biểu đồ
        model.addAttribute("chartData", dashboardService.getRevenueChartData());
        // Lấy 5 nhà hàng mới nhất (cho bảng nhỏ dashboard)
        model.addAttribute("restaurants", dashboardService.getNewRestaurants());
        // Lấy log hoạt động
        model.addAttribute("activities", dashboardService.getRecentActivities());

        return "admin/dashboard";
    }

    // =========================================================
    // 3. TRANG QUẢN LÝ NHÀ HÀNG (FULL CHỨC NĂNG)
    // =========================================================
    @GetMapping("/restaurants")
    public String restaurantManagement(Model model) {
        // Lấy toàn bộ danh sách nhà hàng (để render vào bảng JS)
        // Biến này tên là 'restaurantList' để khớp với th:inline="javascript" bên file HTML
        model.addAttribute("restaurantList", restaurantService.getAllRestaurants());

        // Lấy số liệu thống kê (3 thẻ bài trên cùng trang này)
        model.addAttribute("totalCount", restaurantService.getTotalCount());
        model.addAttribute("pendingCount", restaurantService.getPendingCount());
        model.addAttribute("avgRating", restaurantService.getAvgRating());

        return "admin/restaurant-management";
    }

    // =========================================================
    // 4. CÁC TRANG KHÁC (Chưa có Service, giữ nguyên tĩnh)
    // =========================================================

    @GetMapping("/users")
    public String userManagement() {
        return "admin/user-management";
    }

    @GetMapping("/revenue")
    public String systemRevenue() {
        return "admin/system-revenue";
    }

    @GetMapping("/ai-config")
    public String aiConfig() {
        return "admin/ai-config";
    }

    @GetMapping("/monitor")
    public String systemMonitor() {
        return "admin/system-monitor";
    }
}