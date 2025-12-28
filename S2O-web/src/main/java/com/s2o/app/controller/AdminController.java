package com.s2o.app.controller;

import com.s2o.app.service.AdminDashboardService; // Đã đổi import
import com.s2o.app.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // --- 1. INJECT CÁC SERVICE ---

    @Autowired
    private AdminDashboardService adminDashboardService; // Đã đổi tên biến

    @Autowired
    private RestaurantService restaurantService;

    // =========================================================
    // 2. TRANG DASHBOARD TỔNG QUAN
    // =========================================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Gọi hàm từ adminDashboardService
        model.addAttribute("stats", adminDashboardService.getStats());
        model.addAttribute("chartData", adminDashboardService.getRevenueChartData());
        model.addAttribute("restaurants", adminDashboardService.getNewRestaurants());
        model.addAttribute("activities", adminDashboardService.getRecentActivities());

        return "admin/dashboard";
    }

    // =========================================================
    // 3. TRANG QUẢN LÝ NHÀ HÀNG
    // =========================================================
    @GetMapping("/restaurants")
    public String restaurantManagement(Model model) {
        // Phần này gọi RestaurantService (giữ nguyên)
        model.addAttribute("restaurantList", restaurantService.getAllRestaurants());
        model.addAttribute("totalCount", restaurantService.getTotalCount());
        model.addAttribute("pendingCount", restaurantService.getPendingCount());
        model.addAttribute("avgRating", restaurantService.getAvgRating());

        return "admin/restaurant-management";
    }

    // =========================================================
    // 4. CÁC TRANG KHÁC (Static)
    // =========================================================

    @GetMapping("/users")
    public String userManagement() { return "admin/user-management"; }

    @GetMapping("/revenue")
    public String systemRevenue() { return "admin/system-revenue"; }

    @GetMapping("/ai-config")
    public String aiConfig() { return "admin/ai-config"; }

    @GetMapping("/monitor")
    public String systemMonitor() { return "admin/system-monitor"; }
}