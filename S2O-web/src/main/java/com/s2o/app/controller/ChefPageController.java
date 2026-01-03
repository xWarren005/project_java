package com.s2o.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chef")
public class ChefPageController {
    //URL: http://localhost:8080/chef/dashboard
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "chef/dashboard-chef";
    }
}