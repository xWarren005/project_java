package com.s2o.app.controller;

import com.s2o.app.service.AdminDashboardService;
import com.s2o.app.service.AdminRestaurantService;
import com.s2o.app.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // =========================================================
    // 1. INJECT CÁC SERVICE
    // =========================================================

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Autowired
    private AdminRestaurantService adminRestaurantService; // Quản lý Nhà hàng

    @Autowired
    private AdminUserService adminUserService; // Quản lý User (MỚI)


    // =========================================================
    // 2. TRANG DASHBOARD TỔNG QUAN
    // =========================================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
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
        // Lấy danh sách nhà hàng
        model.addAttribute("restaurantList", adminRestaurantService.getAllRestaurants());

        // Lấy số liệu thống kê
        model.addAttribute("totalCount", adminRestaurantService.getTotalCount());
        model.addAttribute("pendingCount", adminRestaurantService.getPendingCount());
        model.addAttribute("avgRating", adminRestaurantService.getAvgRating());

        return "admin/restaurant-management";
    }


    // =========================================================
    // 4. TRANG QUẢN LÝ USER & PHÂN QUYỀN
    // =========================================================
    @GetMapping("/users")
    public String userManagement(Model model) {
        // Lấy danh sách User để render bảng
        model.addAttribute("userList", adminUserService.getAllUsers());

        // Lấy số liệu thống kê (3 thẻ bài đầu trang)
        model.addAttribute("totalUsers", adminUserService.getTotalUsers());
        model.addAttribute("activeCount", adminUserService.getActiveUsers());
        model.addAttribute("newCount", adminUserService.getNewUsersThisWeek());

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