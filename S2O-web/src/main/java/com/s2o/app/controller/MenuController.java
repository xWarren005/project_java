package com.s2o.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class MenuController {
    @GetMapping("/menuuser")
    public String menuuserPage() {
        return "user/menuuser";
    }
}