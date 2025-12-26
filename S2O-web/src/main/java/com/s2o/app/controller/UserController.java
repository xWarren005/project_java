package com.s2o.app.controller;

import com.s2o.app.entity.User;
import com.s2o.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =============================
    // HIỂN THỊ TRANG LOGIN
    // =============================
    @GetMapping("/login")
    public String loginPage() {
        return "user/login"; // templates/user/login.html
    }

    // =============================
    // XỬ LÝ LOGIN (FORM SUBMIT)
    // =============================
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String username, // lấy từ input name="username"
            @RequestParam String password, // lấy từ input name="password"
            Model model,
            HttpSession session
    ) {

        // Gọi service xử lý login
        User user = userService.login(username, password);

        if (user == null) {
            // Login thất bại → trả về login + báo lỗi
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
            return "user/login";
        }
        // Login thành công → lưu session
        session.setAttribute("LOGIN_USER", user);
        return "redirect:/user/menuuser";
    }
    // =============================
    // LOGOUT
    // =============================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa session
        return "redirect:/user/login";
    }
}
