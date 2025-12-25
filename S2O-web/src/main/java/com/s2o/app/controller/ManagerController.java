package com.s2o.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    // Trang Thống kê
    // URL: http://localhost:8080/manager/overview
    @GetMapping("/overview")
    public String overviewPage() {
        return "manager/overview";
    }

    // Trang Quản lý món ăn
    // URL: http://localhost:8080/manager/dishes
    @GetMapping("/dishes")
    public String dishesPage() {
        return "manager/dishes";
    }

    // Trang Quản lý bàn
    // URL: http://localhost:8080/manager/tables
    @GetMapping("/tables")
    public String tablesPage() {
        return "manager/tables";
    }
}