package com.s2o.app.controller;

import com.s2o.app.dto.SystemLoginRequest;
import com.s2o.app.entity.User;
import com.s2o.app.service.AdminUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/system")
public class SystemAuthController {

    @Autowired
    private AdminUserService adminUserService;

    //  Xử lý Form đăng nhập (POST)
    @PostMapping("/login")
    public String processSystemLogin(@ModelAttribute SystemLoginRequest loginRequest,
                                     HttpSession session,
                                     Model model) {

        // Gọi Service kiểm tra: Username + Pass + Role
        User user = adminUserService.loginSystem(
                loginRequest.getUsername(),
                loginRequest.getPassword(),
                loginRequest.getRole()
        );

        if (user == null) {
            // Đăng nhập thất bại
            model.addAttribute("error", "Sai thông tin đăng nhập hoặc sai vai trò!");
            return "admin/login"; // Trả về trang đăng nhập kèm lỗi
        }

        // Đăng nhập thành công -> Lưu vào Session
        session.setAttribute("currentUser", user);

        // Điều hướng dựa trên Role (Phân quyền chuyển trang)
        String role = user.getRole();

        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            return "redirect:/admin/dashboard"; // Admin/Manager vào Dashboard thống kê
        } else if ("CHEF".equals(role)) {
            return "redirect:/chef/dashboard";     // Đầu bếp vào trang Bếp
        } else if ("CASHIER".equals(role)) {
            return "redirect:/cashier/tables";     // Thu ngân vào trang POS
        }

        // Mặc định
        return "redirect:/admin/dashboard";
    }

    // 3. Đăng xuất
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa session
        return "redirect:/system/login";
    }
}