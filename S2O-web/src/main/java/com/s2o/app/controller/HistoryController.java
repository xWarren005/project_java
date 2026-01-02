package com.s2o.app.controller;

import com.s2o.app.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class HistoryController {

    // URL: /user/history
    // Nhiệm vụ: Trả về file HTML giao diện lịch sử
    @GetMapping("/history")
    public String historyPage(HttpSession session, Model model) {
        // 1. Kiểm tra đăng nhập
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) {
            return "redirect:/user/login"; // Chưa đăng nhập thì đá về trang login
        }

        // 2. Truyền thông tin User vào Model (để hiển thị tên, avatar trên Header nếu cần)
        model.addAttribute("currentUser", user);

        // 3. Trả về tên file HTML trong thư mục templates/user/history.html
        return "user/history";
    }
}