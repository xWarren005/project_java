package com.s2o.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cashier")
public class CashierController {

    // URL: http://localhost:8080/cashier/tables
    @GetMapping("/tables")
    public String showDashboard() {
        return "cashier/tables";
    }
}
