package com.s2o.app.controller;

import com.s2o.app.service.GuestOrderTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest/tracking") // URL riêng cho Guest Tracking
public class GuestOrderTrackingController {

    @Autowired
    private GuestOrderTrackingService GuestOrderTrackingService;

    // API: Lấy danh sách món đã gọi của bàn (Thay thế cho getMyOrders của User)
    // URL: GET /api/guest/tracking/{tableId}
    @GetMapping("/{tableId}")
    public ResponseEntity<?> getGuestOrders(@PathVariable Integer tableId) {

        // Không cần check Session
        // Chỉ cần TableID hợp lệ (Service sẽ lo việc query DB)
        try {
            // Hàm getOrdersByTableId đã viết trong GuestService (trả về OrderTrackingResponse)
            return ResponseEntity.ok(GuestOrderTrackingService  .getOrdersByTableId(tableId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // Lưu ý: Khách vãng lai thường không cần tách riêng API /history (đã thanh toán)
    // vì hàm getOrdersByTableId ở trên thường trả về TẤT CẢ món trong phiên (cả đang nấu và đã trả tiền).
    // Tuy nhiên, nếu bạn muốn tách riêng 1 API chỉ lấy món ĐÃ THANH TOÁN (PAID) cho Guest:
    /*
    @GetMapping("/paid/{tableId}")
    public ResponseEntity<?> getGuestPaidHistory(@PathVariable Integer tableId) {
        // Bạn sẽ cần viết thêm hàm getPaidOrdersByTableId trong GuestService
        return ResponseEntity.ok(guestService.getPaidOrdersByTableId(tableId));
    }
    */
}