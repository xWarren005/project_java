package com.s2o.app.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class MenuController {
    @GetMapping("/menuuser")
    public String menuuserPage(HttpSession session, Model model) {
        Integer restaurantId =
                (Integer) session.getAttribute("RESTAURANT_ID");
        String tableName =
                (String) session.getAttribute("TABLE_NAME");
        Integer tableId = (Integer) session.getAttribute("TABLE_ID");

        if (restaurantId == null) {
            return "redirect:welcome?tableId=1";
        }
        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("tableId", tableId);
        model.addAttribute("tableName", tableName);
        return "user/menuuser";
    }
}