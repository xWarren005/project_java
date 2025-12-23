package com.s2o.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager") // Định nghĩa đường dẫn gốc là /manager
public class ManagerController {

    // Khi người dùng vào: http://localhost:8080/manager/overview
    @GetMapping("/overview")
    public String overviewPage() {
        // Trả về file: src/main/resources/templates/manager/overview.html
        return "manager/overview";
    }

    // Bạn có thể map thêm các trang khác tương tự:
    // @GetMapping("/dishes")
    // public String dishesPage() { return "manager/dishes"; }
}