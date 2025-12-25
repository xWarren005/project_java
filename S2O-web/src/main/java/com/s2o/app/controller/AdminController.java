package com.s2o.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // 1. Trang Dashboard Tổng quan
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    // 2. Trang Quản lý Nhà hàng
    @GetMapping("/restaurants")
    public String restaurantManagement() {
        return "admin/restaurant-management";
    }

    // 3. Trang Người dùng & Phân quyền
    @GetMapping("/users")
    public String userManagement() {
        return "admin/user-management";
    }

    // 4. Trang Doanh thu
    @GetMapping("/revenue")
    public String systemRevenue() {
        return "admin/system-revenue";
    }

    // 5. Trang Cấu hình AI
    @GetMapping("/ai-config")
    public String aiConfig() {
        return "admin/ai-config";
    }

    // 6. Trang Giám sát hệ thống
    @GetMapping("/monitor")
    public String systemMonitor() {
        return "admin/system-monitor";
    }
}