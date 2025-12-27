package com.s2o.app.service;

import com.s2o.app.dto.RestaurantDTO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    public List<RestaurantDTO> getAllRestaurants() {
        List<RestaurantDTO> list = new ArrayList<>();
        // Sử dụng Constructor 1 (có ID và Rating)
        list.add(new RestaurantDTO(1L, "Phở 24", "123 Lê Lợi, Q1, TP.HCM", "active", 4.5));
        list.add(new RestaurantDTO(2L, "Sushi World", "456 Nguyễn Huệ, Q3, TP.HCM", "active", 4.8));
        list.add(new RestaurantDTO(3L, "BBQ House", "789 Võ Văn Tần, Q3, TP.HCM", "pending", null)); // null rating
        list.add(new RestaurantDTO(4L, "Vegan Garden", "321 Pasteur, Q7, TP.HCM", "active", 4.3));
        list.add(new RestaurantDTO(5L, "Pizza Express", "654 Hai Bà Trưng, Q1, TP.HCM", "inactive", 4.1));
        return list;
    }

    // Các hàm thống kê giả
    public int getTotalCount() { return 248; }
    public int getPendingCount() { return 8; }
    public double getAvgRating() { return 4.6; }
}