package com.s2o.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // CHỈ TRẢ VỀ TÊN FILE HTML, KHÔNG GỌI SERVICE LẤY DỮ LIỆU Ở ĐÂY NỮA

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard"; // Trả về templates/admin/dashboard.html
    }

    @GetMapping("/restaurants")
    public String restaurantManagement() {
        return "admin/restaurant-management";
    }

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