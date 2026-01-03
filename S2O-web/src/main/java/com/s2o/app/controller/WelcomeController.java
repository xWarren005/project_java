package com.s2o.app.controller;

import com.s2o.app.entity.RestaurantTable;
import com.s2o.app.repository.TableRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class WelcomeController {
    private final TableRepository tableRepository;
    public WelcomeController(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }
    @GetMapping("/welcome")
    public String welcomePage(
            @RequestParam("tableId") Integer tableId,
            HttpSession session,
            Model model
    ) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElse(null);
        if (table == null) {
            model.addAttribute("error", "Bàn không tồn tại");
            return "error";
        }
        // 2. Lưu thông tin bàn + nhà hàng vào session
        session.setAttribute("TABLE_ID", table.getId());
        session.setAttribute("TABLE_NAME", table.getTableName());
        session.setAttribute("RESTAURANT_ID", table.getRestaurantId());
// 3. Truyền dữ liệu ra view
        model.addAttribute("tableName", table.getTableName());
        model.addAttribute("restaurantId", table.getRestaurantId());
        model.addAttribute("tableId", table.getId());
        return "user/welcome";
    }
}