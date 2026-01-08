package com.s2o.app.service;

import com.s2o.app.dto.RestaurantDTO;
import com.s2o.app.entity.Restaurant;
import com.s2o.app.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminRestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    // 1. Lấy danh sách nhà hàng (Convert Entity -> DTO)
    public List<RestaurantDTO> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        // Sắp xếp: ID giảm dần (Mới nhất lên đầu)
        restaurants.sort((r1, r2) -> r2.getId().compareTo(r1.getId()));

        return restaurants.stream().map(r -> new RestaurantDTO(
                r.getId(),
                r.getName(),
                r.getAddress(),
                // Trả về trạng thái duyệt (PENDING/APPROVED/REJECTED)
                r.getApprovalStatus() != null ? r.getApprovalStatus() : "UNKNOWN",
                r.getRating() != null ? r.getRating() : 0.0
        )).collect(Collectors.toList());
    }

    // 2. Thống kê: Tổng số nhà hàng
    public long getTotalCount() {
        return restaurantRepository.count();
    }

    // 3. Thống kê: Số lượng chờ duyệt
    public long getPendingCount() {
        return restaurantRepository.countByApprovalStatus("PENDING");
    }

    // 4. Thống kê: Điểm đánh giá trung bình
    public double getAvgRating() {
        List<Restaurant> list = restaurantRepository.findAll();
        if (list.isEmpty()) return 0.0;

        double sum = list.stream()
                .mapToDouble(r -> r.getRating() != null ? r.getRating() : 0.0)
                .sum();

        // Làm tròn 1 chữ số thập phân
        return Math.round((sum / list.size()) * 10.0) / 10.0;
    }

    // ========================================================
    // CÁC HÀM XỬ LÝ NGHIỆP VỤ (CRUD) - MỚI BỔ SUNG
    // ========================================================

    // 5. Thêm nhà hàng mới
    public void createRestaurant(RestaurantDTO dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());

        // Thiết lập mặc định
        restaurant.setApprovalStatus("PENDING"); // Mới tạo thì chờ duyệt
        restaurant.setIsActive(false);           // Chưa kích hoạt
        restaurant.setRating(0.0);               // Chưa có đánh giá

        // Lưu vào DB
        restaurantRepository.save(restaurant);
    }

    // 6. Duyệt nhà hàng
    public void approveRestaurant(long id) {
        Optional<Restaurant> optional = restaurantRepository.findById(id);
        if (optional.isPresent()) {
            Restaurant r = optional.get();
            r.setApprovalStatus("APPROVED"); // Đổi sang Đã duyệt
            r.setIsActive(true);             // Kích hoạt hoạt động
            restaurantRepository.save(r);
        }
    }

    // 7. Xóa nhà hàng
    public void deleteRestaurant(long id) {
        if (restaurantRepository.existsById(id)) {
            restaurantRepository.deleteById(id);
        }
    }
    // 8. Cập nhật thông tin nhà hàng
    public void updateRestaurant(long id, RestaurantDTO dto) {
        Optional<Restaurant> optional = restaurantRepository.findById(id);
        if (optional.isPresent()) {
            Restaurant r = optional.get();
            // Chỉ cập nhật các trường cho phép sửa
            r.setName(dto.getName());
            r.setAddress(dto.getAddress());
            // Có thể thêm logic sửa số điện thoại, mô tả... tại đây

            restaurantRepository.save(r);
        }
    }
}