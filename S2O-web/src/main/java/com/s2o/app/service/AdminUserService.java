package com.s2o.app.service;

import com.s2o.app.dto.UserDTO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminUserService {

    private List<UserDTO> userList = new ArrayList<>();

    public AdminUserService() {
        // Mock Data (Dữ liệu giả lập giống trong JS của bạn)
        userList.add(new UserDTO(1L, "Nguyễn Văn A", "admin@system.com", "Admin", "active"));
        userList.add(new UserDTO(2L, "Trần Thị B", "manager@pho24.com", "Restaurant Manager", "active"));
        userList.add(new UserDTO(3L, "Lê Văn C", "user@email.com", "Customer", "active"));
        userList.add(new UserDTO(4L, "Phạm Thị D", "manager@sushi.com", "Restaurant Manager", "active"));
        userList.add(new UserDTO(5L, "Hoàng Văn E", "user2@email.com", "Customer", "inactive"));

        // Thêm vài user nữa để số liệu nhìn đẹp hơn
        userList.add(new UserDTO(6L, "Admin Support", "support@system.com", "Admin", "active"));
        userList.add(new UserDTO(7L, "Guest User", "guest@temp.com", "Customer", "inactive"));
    }

    // 1. Lấy danh sách user
    public List<UserDTO> getAllUsers() {
        return userList;
    }

    // 2. Thống kê: Tổng User
    public int getTotalUsers() {
        return userList.size(); // Hoặc con số giả định: 12458
    }

    // 3. Thống kê: User đang hoạt động (active)
    public long getActiveUsers() {
        return userList.stream().filter(u -> "active".equals(u.getStatus())).count();
        // Hoặc giả định: 8291
    }

    // 4. Thống kê: User mới tuần này (Giả định vì DTO chưa có trường ngày tạo)
    public int getNewUsersThisWeek() {
        return 156; // Trả về số cứng giả lập
    }
}