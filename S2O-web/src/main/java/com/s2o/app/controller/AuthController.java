package com.s2o.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth") // Gom nhóm các URL liên quan đến xác thực
public class AuthController {
    // Trang đăng nhập cho admin (nếu tách riêng)
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        // Trả về file template: src/main/resources/templates/admin/login.html
        return "admin/login";
    }
}