package com.s2o.app.controller;

import com.s2o.app.dto.response.ManagerOverviewResponse;
import com.s2o.app.service.ManagerDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager")
public class ManagerRestController {

    @Autowired
    private ManagerDashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<ManagerOverviewResponse> getOverview() {
        // Trong thực tế, lấy restaurantId từ SecurityContext (User đang đăng nhập)
        Integer currentRestaurantId = 1;

        ManagerOverviewResponse data = dashboardService.getDashboardData(currentRestaurantId);
        return ResponseEntity.ok(data);
    }
}