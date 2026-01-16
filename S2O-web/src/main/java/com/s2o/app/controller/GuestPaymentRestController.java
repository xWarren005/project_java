package com.s2o.app.controller;

import com.s2o.app.service.GuestPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/guest/payment")
public class GuestPaymentRestController {

    @Autowired
    private GuestPaymentService GuestPaymentService;

    // API: Yêu cầu thanh toán (Khách vãng lai)
    // POST /api/guest/payment/checkout
    @PostMapping("/checkout")
    public ResponseEntity<?> requestCheckout(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Lấy thông tin từ Payload (Do Guest không có Session)
            if (payload.get("tableId") == null) {
                return ResponseEntity.badRequest().body("Thiếu thông tin bàn (tableId)");
            }

            Integer tableId = Integer.parseInt(payload.get("tableId").toString());
            String method = payload.get("method").toString(); // "CASH" hoặc "TRANSFER"

            // 2. Gọi Service xử lý
            // Hàm này sẽ chuyển trạng thái các đơn món sang "PAYMENT_PENDING"
            GuestPaymentService.processGuestPayment(tableId, method);

            return ResponseEntity.ok("Đã gửi yêu cầu thanh toán. Vui lòng đợi nhân viên.");

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID bàn không hợp lệ");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thanh toán: " + e.getMessage());
        }
    }
}