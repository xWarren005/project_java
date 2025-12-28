package com.s2o.app.service;

import com.s2o.app.dto.ActivityLogDTO;
import com.s2o.app.dto.RestaurantDTO;
import com.s2o.app.dto.StatItem;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminDashboardService {

    // 1. Thống kê 4 thẻ bài (Stats Cards)
    public List<StatItem> getStats() {
        return Arrays.asList(
                new StatItem("Nhà hàng Hoạt động", "248", "↗ +12.5%", true, "store", "icon-blue"),
                new StatItem("Tổng Người dùng", "12,458", "↗ +8.2%", true, "users", "icon-purple"),
                new StatItem("Doanh thu Tháng", "$48,392", "↗ +15.3%", true, "dollar-sign", "icon-green"),
                new StatItem("Uptime Hệ thống", "99.8%", "↘ -0.1%", false, "activity", "icon-pink")
        );
    }

    // 2. Dữ liệu biểu đồ (Chart Data)
    public List<Integer> getRevenueChartData() {
        return Arrays.asList(15000, 22000, 18000, 30000, 35000, 48392);
    }

    // 3. Danh sách nhà hàng mới (Widget bên trái)
    public List<RestaurantDTO> getNewRestaurants() {
        return Arrays.asList(
                new RestaurantDTO("Phở 24", "Quận 1, TP.HCM", "2 giờ trước", "Hoạt động", "success"),
                new RestaurantDTO("Sushi World", "Quận 3, TP.HCM", "5 giờ trước", "Hoạt động", "success"),
                new RestaurantDTO("BBQ House", "Cầu Giấy, HN", "1 ngày trước", "Đóng cửa", "error"),
                new RestaurantDTO("Kichi Kichi", "Bình Thạnh", "2 ngày trước", "Hoạt động", "success"),
                new RestaurantDTO("Pizza 4P's", "Đà Nẵng", "3 ngày trước", "Đang xét duyệt", "warning")
        );
    }

    // 4. Hoạt động gần đây (Widget bên phải)
    public List<ActivityLogDTO> getRecentActivities() {
        return Arrays.asList(
                new ActivityLogDTO("14:30", "admin@system.com", "Phê duyệt", "Duyệt nhà hàng Phở 24", "Thành công", "success"),
                new ActivityLogDTO("13:20", "user123@gmail.com", "Đặt bàn", "Đặt bàn 4 người", "Thành công", "success"),
                new ActivityLogDTO("12:50", "manager@bbq.com", "Menu", "Thêm món sườn nướng", "Thành công", "success"),
                new ActivityLogDTO("11:15", "system@bot", "Backup", "Sao lưu dữ liệu", "Cảnh báo", "warning"),
                new ActivityLogDTO("09:30", "guest@email.com", "Login", "Sai mật khẩu 3 lần", "Lỗi", "error")
        );
    }
}