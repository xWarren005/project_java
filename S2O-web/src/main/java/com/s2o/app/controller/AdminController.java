package com.s2o.app.controller;

import com.s2o.app.service.AdminDashboardService;
import com.s2o.app.service.AdminRestaurantService;
import com.s2o.app.service.AdminUserService;
import com.s2o.app.service.AdminRevenueService;
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
    private AdminRestaurantService adminRestaurantService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminRevenueService adminRevenueService;


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
        // Danh sách nhà hàng
        model.addAttribute("restaurantList", adminRestaurantService.getAllRestaurants());

        // Số liệu thống kê
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
        // Danh sách User
        model.addAttribute("userList", adminUserService.getAllUsers());

        // Số liệu thống kê
        model.addAttribute("totalUsers", adminUserService.getTotalUsers());
        model.addAttribute("activeCount", adminUserService.getActiveUsers());
        model.addAttribute("newCount", adminUserService.getNewUsersThisWeek());

        return "admin/user-management";
    }


    // =========================================================
    // 5. TRANG DOANH THU HỆ THỐNG (MỚI CẬP NHẬT)
    // =========================================================
    @GetMapping("/revenue")
    public String systemRevenue(Model model) {
        // Đẩy danh sách giao dịch sang View
        model.addAttribute("transactionList", adminRevenueService.getAllTransactions());

        // Đẩy các con số thống kê (đã format tiền tệ $)
        model.addAttribute("todayRev", adminRevenueService.getTodayRevenue());
        model.addAttribute("monthRev", adminRevenueService.getMonthRevenue());
        model.addAttribute("predictRev", adminRevenueService.getPredictedRevenue());

        return "admin/system-revenue";
    }


    // =========================================================
    // 6. CÁC TRANG KHÁC (Chưa có logic, giữ nguyên tĩnh)
    // =========================================================

    @GetMapping("/ai-config")
    public String aiConfig() {
        return "admin/ai-config";
    }

    @GetMapping("/monitor")
    public String systemMonitor() {
        return "admin/system-monitor";
    }
}