package com.s2o.app.service;

import com.s2o.app.dto.UserOrderRequest;
import com.s2o.app.dto.response.MenuResponse;
import com.s2o.app.entity.*;
import com.s2o.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserMenuService {

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderRepository orderRepo;

    // --- LOGIC 1: LẤY MENU ---
    public MenuResponse getMenuData(Integer restaurantId) {
        MenuResponse response = new MenuResponse();

        // 1. Lấy danh mục
        List<Category> cats = categoryRepo.findAll();
        List<MenuResponse.CategoryDTO> catList = new ArrayList<>();

        // Thêm mục "Tất cả"
        MenuResponse.CategoryDTO all = new MenuResponse.CategoryDTO();
        all.setId("all");
        all.setName("Tất cả");
        catList.add(all);

        for (Category c : cats) {
            if (c.getRestaurantId().equals(restaurantId)) {
                MenuResponse.CategoryDTO dto = new MenuResponse.CategoryDTO();
                dto.setId(String.valueOf(c.getId()));
                dto.setName(c.getName());
                catList.add(dto);
            }
        }
        response.setCategories(catList);

        // 2. Lấy món ăn
        List<Product> products = productRepo.findAll();
        List<MenuResponse.DishDTO> dishList = new ArrayList<>();

        for (Product p : products) {
            // Logic lọc: Đúng nhà hàng + Đang bán (isAvailable)
            if (p.getCategory() != null &&
                    p.getCategory().getRestaurantId().equals(restaurantId) &&
                    Boolean.TRUE.equals(p.getIsAvailable())) {

                MenuResponse.DishDTO dto = new MenuResponse.DishDTO();
                dto.setId(String.valueOf(p.getId()));
                dto.setName(p.getName());
                dto.setDescription(p.getDescription());
                dto.setPrice(p.getPrice());
                // Xử lý ảnh null
                dto.setImage(p.getImageUrl() != null ? p.getImageUrl() : "/images/default-food.png");
                dto.setCategory(String.valueOf(p.getCategory().getId())); // Quan trọng để lọc tab

                dishList.add(dto);
            }
        }
        response.setMenuItems(dishList);
        return response;
    }

    // --- LOGIC 2: ĐẶT MÓN (Transactional đảm bảo toàn vẹn dữ liệu) ---
    @Transactional
    public void createOrder(User user, UserOrderRequest request) throws Exception {
        Order order = new Order();
        order.setUserId(user.getId());
        order.setRestaurantId(request.getRestaurantId());
        order.setTableId(request.getTableId());
        order.setNote(request.getNote());
        order.setStatus("PENDING"); // Trạng thái chờ bếp xác nhận
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (UserOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Optional<Product> pOpt = productRepo.findById(itemReq.getProductId());

            if (pOpt.isPresent()) {
                Product p = pOpt.get();
                OrderItem oi = new OrderItem();
                oi.setOrder(order); // Liên kết khóa ngoại
                oi.setProduct(p);
                oi.setQuantity(itemReq.getQuantity());
                oi.setUnitPrice(p.getPrice()); // Lưu giá tại thời điểm đặt (đề phòng giá đổi sau này)

                // Cộng dồn tổng tiền
                total = total.add(p.getPrice().multiply(new BigDecimal(itemReq.getQuantity())));
                orderItems.add(oi);
            }
        }

        if (orderItems.isEmpty()) {
            throw new Exception("Không tìm thấy món ăn nào hợp lệ");
        }

        order.setTotalAmount(total);
        order.setItems(orderItems); // Hibernate Cascade sẽ tự lưu List<OrderItem>

        orderRepo.save(order);
    }
}