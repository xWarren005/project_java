package com.s2o.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s2o.app.dto.BankQrConfigDTO;
import com.s2o.app.dto.RestaurantDTO;
import com.s2o.app.entity.Restaurant;
import com.s2o.app.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // =================================================================
    // PHẦN CŨ (giangan-ng): MOCK DATA CHO DANH SÁCH & THỐNG KÊ
    // =================================================================

    public List<RestaurantDTO> getAllRestaurants() {
        List<RestaurantDTO> list = new ArrayList<>();
        list.add(new RestaurantDTO(1L, "Phở 24", "123 Lê Lợi, Q1, TP.HCM", "active", 4.5));
        list.add(new RestaurantDTO(2L, "Sushi World", "456 Nguyễn Huệ, Q3, TP.HCM", "active", 4.8));
        list.add(new RestaurantDTO(3L, "BBQ House", "789 Võ Văn Tần, Q3, TP.HCM", "pending", null));
        list.add(new RestaurantDTO(4L, "Vegan Garden", "321 Pasteur, Q7, TP.HCM", "active", 4.3));
        list.add(new RestaurantDTO(5L, "Pizza Express", "654 Hai Bà Trưng, Q1, TP.HCM", "inactive", 4.1));
        return list;
    }

    public int getTotalCount() { return 248; }
    public int getPendingCount() { return 8; }
    public double getAvgRating() { return 4.6; }

    // =================================================================
    // PHẦN MỚI: LOGIC QR CODE (LƯU VÀO DB THẬT)
    // =================================================================

    /**
     * Lấy cấu hình QR từ DB.
     * Mặc định lấy nhà hàng ID = 1 (do database bạn đang có 1 nhà hàng ID=1)
     */
    public BankQrConfigDTO getQrConfig(String username) {
        // Tạm thời hardcode lấy ID = 1 vì hệ thống chưa có login đa user phức tạp
        Restaurant restaurant = restaurantRepository.findById(1L).orElse(null);

        if (restaurant != null && restaurant.getBankQrConfig() != null) {
            try {
                return objectMapper.readValue(restaurant.getBankQrConfig(), BankQrConfigDTO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new BankQrConfigDTO();
    }

    /**
     * Lưu cấu hình QR vào DB cho nhà hàng ID = 1
     */
    public void updateQrConfig(String username, BankQrConfigDTO configDTO) {
        // Tìm nhà hàng số 1
        Restaurant restaurant = restaurantRepository.findById(1L).orElseGet(() -> {
            // Nếu chưa có (dù file SQL có rồi), tạo mới để tránh lỗi null
            Restaurant newRes = new Restaurant();
            newRes.setName("Nhà hàng S2O");
            newRes.setAddress("Mặc định");
            newRes.setIsActive(true);
            return restaurantRepository.save(newRes);
        });

        try {
            String jsonString = objectMapper.writeValueAsString(configDTO);
            restaurant.setBankQrConfig(jsonString);
            restaurantRepository.save(restaurant);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu cấu hình QR: " + e.getMessage(), e);
        }
    }
}