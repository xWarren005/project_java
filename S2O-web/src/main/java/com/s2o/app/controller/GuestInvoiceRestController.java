package com.s2o.app.controller;

import com.s2o.app.service.GuestInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest/invoice")
public class GuestInvoiceRestController {

    @Autowired
    private GuestInvoiceService GuestInvoiceService;

    /**
     * API: Lấy chi tiết hóa đơn tạm tính của bàn
     * URL: GET /api/guest/invoice/{tableId}
     */
    @GetMapping("/{tableId}")
    public ResponseEntity<?> getGuestInvoice(@PathVariable Integer tableId) {
        // Tận dụng hàm lấy danh sách đơn của Service
        // Vì bản chất hóa đơn tạm tính là tổng hợp các món đang phục vụ
        try {
            return ResponseEntity.ok(GuestInvoiceService.getGuestInvoice(tableId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tải hóa đơn: " + e.getMessage());
        }
    }
}