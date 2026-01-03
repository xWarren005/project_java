package com.s2o.app.controller;

import com.s2o.app.dto.response.OrderHistoryResponse;
import com.s2o.app.entity.User;
import com.s2o.app.service.InvoiceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/invoice")
public class InvoiceRestController {

    @Autowired
    private InvoiceService invoiceService;

    // API lấy hóa đơn tạm tính (Các món đã gọi chưa thanh toán)
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentInvoice(HttpSession session) {
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) return ResponseEntity.status(401).body(null);

        return ResponseEntity.ok(invoiceService.getCurrentSessionOrders(user.getId()));
    }
}