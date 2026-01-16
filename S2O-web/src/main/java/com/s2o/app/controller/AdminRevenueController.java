package com.s2o.app.controller;

import com.s2o.app.service.AdminRevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin") // Prefix chung cho các API admin
public class AdminRevenueController {

    @Autowired
    private AdminRevenueService adminRevenueService;

    // ============================================================
    // API: Lấy dữ liệu Doanh thu & Hoa hồng
    // URL: http://localhost:8080/api/admin/revenue
    // ============================================================
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueData() {
        // Gọi Service để tính toán số liệu
        return ResponseEntity.ok(adminRevenueService.getRevenueData());
    }
}