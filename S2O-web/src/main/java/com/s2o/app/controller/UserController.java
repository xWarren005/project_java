package com.s2o.app.controller;

import com.s2o.app.dto.RegisterRequest;
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

    // 1. Hiển thị trang Đăng ký
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "user/register";
    }

    // 2. Xử lý Đăng ký
    @PostMapping("/register")
    public String handleRegister(@ModelAttribute RegisterRequest registerRequest, Model model) {

        // Gọi Service xử lý
        String result = userService.registerUser(registerRequest);

        if ("SUCCESS".equals(result)) {
            // Đăng ký thành công -> Chuyển sang trang login và báo thành công
            return "redirect:/user/login?success=true";
        } else {
            // Đăng ký thất bại -> Ở lại trang register và báo lỗi
            model.addAttribute("error", result);
            return "user/register";
        }
    }
}
