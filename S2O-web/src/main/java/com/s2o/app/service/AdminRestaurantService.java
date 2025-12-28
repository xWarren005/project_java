package com.s2o.app.service;

import com.s2o.app.dto.RestaurantDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminRestaurantService {

    // List giả lập database
    private List<RestaurantDTO> restaurantList = new ArrayList<>();

    public AdminRestaurantService() {
        // Sử dụng Constructor 1 (ID, Name, Address, Status, Rating)
        restaurantList.add(new RestaurantDTO(1L, "Phở 24", "123 Lê Lợi, Q1, TP.HCM", "active", 4.5));
        restaurantList.add(new RestaurantDTO(2L, "Sushi World", "456 Nguyễn Huệ, Q3, TP.HCM", "active", 4.8));
        restaurantList.add(new RestaurantDTO(3L, "BBQ House", "789 Võ Văn Tần, Q3, TP.HCM", "pending", null));
        restaurantList.add(new RestaurantDTO(4L, "Vegan Garden", "321 Pasteur, Q7, TP.HCM", "active", 4.3));
        restaurantList.add(new RestaurantDTO(5L, "Pizza Express", "654 Hai Bà Trưng, Q1, TP.HCM", "inactive", 4.1));

        // Thêm dữ liệu để test
        restaurantList.add(new RestaurantDTO(6L, "Kichi Kichi", "Vincom Đồng Khởi", "active", 4.7));
        restaurantList.add(new RestaurantDTO(7L, "Gogi House", "Saigon Centre", "pending", null));
    }

    // 1. Lấy danh sách nhà hàng
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantList;
    }

    // 2. Tính tổng số quán
    public int getTotalCount() {
        return restaurantList.size();
    }

    // 3. Tính số quán đang chờ duyệt (pending)
    public long getPendingCount() {
        return restaurantList.stream()
                .filter(r -> "pending".equalsIgnoreCase(r.getStatus()))
                .count();
    }

    // 4. Tính điểm đánh giá trung bình
    public double getAvgRating() {
        List<Double> ratings = restaurantList.stream()
                .map(RestaurantDTO::getRating)
                .filter(r -> r != null) // Lọc bỏ null
                .collect(Collectors.toList());

        if (ratings.isEmpty()) return 0.0;

        double sum = ratings.stream().mapToDouble(Double::doubleValue).sum();
        double avg = sum / ratings.size();

        // Làm tròn 1 chữ số thập phân (VD: 4.56 -> 4.6)
        return Math.round(avg * 10.0) / 10.0;
    }
}