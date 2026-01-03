package com.s2o.app.controller;

import com.s2o.app.dto.response.ProfileResponse;
import com.s2o.app.entity.Order;
import com.s2o.app.entity.User;
import com.s2o.app.repository.UserRepository;
import com.s2o.app.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class ProfileRestController {
    private final OrderRepository orderRepository;
    private final UserRepository  userRepository;

    public ProfileRestController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile-data")
    public ResponseEntity<?> getProfileData(HttpSession session) {
        // 1. Kiểm tra đăng nhập
        User currentUser = (User) session.getAttribute("LOGIN_USER");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("vui lòng đăng nhập");
        }

        // Đảm bảo lấy dữ liệu mới nhất từ DB (tránh trường hợp session cũ)
        User dbUser = userRepository.findById(currentUser.getId()).orElse(currentUser);

        ProfileResponse response = new ProfileResponse();

        // 2. Map thông tin User
        ProfileResponse.UserInfoDTO userInfo = new ProfileResponse.UserInfoDTO();
        userInfo.setFullname(dbUser.getFullName());
        userInfo.setEmail(dbUser.getUsername());
        // Nếu DB có cột avatar thì lấy, không thì dùng ảnh mặc định
        userInfo.setAvatar("/images/default-avatar.png"); // Ảnh mặc định hoặc từ DB
        // Logic giả lập hạng thành viên (sau này lấy từ bảng loyalty_memberships)
        userInfo.setRank("Thành viên Bạc"); // Logic phân hạng có thể tính sau
        response.setUser(userInfo);

        // 3. Lấy lịch sử đơn hàng (Lịch của tôi)
        // Giả sử OrderRepository có hàm findByUserIdOrderByCreatedAtDesc
        List<com.s2o.app.entity.Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(dbUser.getId());
        List<ProfileResponse.CalendarItemDTO> calendarList = new ArrayList<>();

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        for (Order order : orders) {
            ProfileResponse.CalendarItemDTO item = new ProfileResponse.CalendarItemDTO();
            item.setId("ORD-" + order.getId());

            if (order.getRestaurant() != null) {
                item.setPlace(order.getRestaurant().getName());
            } else {
                // Fallback nếu dữ liệu cũ bị null hoặc lỗi liên kết
                item.setPlace("Nhà hàng (ID: " + order.getRestaurantId() + ")");
            }
            // ------------------------

            // Format ngày giờ an toàn (tránh null pointer)
            if (order.getCreatedAt() != null) {
                item.setDate(order.getCreatedAt().format(dateFmt));
                item.setTime(order.getCreatedAt().format(timeFmt));
            } else {
                item.setDate("");
                item.setTime("");
            }
            // Xử lý trạng thái để tô màu
            if ("PENDING".equals(order.getStatus())) {
                item.setStatus("upcoming"); // Màu vàng
                item.setStatusText("Chờ xác nhận");
            } else if ("COMPLETED".equals(order.getStatus()) || "PAID".equals(order.getStatus())){
                item.setStatus("finished"); // Màu xám
                item.setStatusText("Hoàn thành");
            } else if ("CANCELLED".equals(order.getStatus())) {
                item.setStatus("finished");
                item.setStatusText("Đã hủy");
            } else {
                item.setStatus("upcoming"); // Màu xanh
                item.setStatusText("Đang phục vụ");
            }

            calendarList.add(item);
        }
        response.setCalendar(calendarList);
        // 4. Map Voucher (Hiện tại giả lập list rỗng hoặc demo vì chưa có bảng Voucher)
        // Nếu bạn muốn lấy từ DB, hãy tạo Entity Voucher và truy vấn tương tự Order
        response.setVouchers(new ArrayList<>());
        return ResponseEntity.ok(response);
    }
}
