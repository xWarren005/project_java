package com.s2o.app.service;

import com.s2o.app.dto.GuestOrderRequest;
import com.s2o.app.dto.response.MenuResponse;
import com.s2o.app.entity.*;
import com.s2o.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GuestMenuService {

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderRepository orderRepo;

    // --- LOGIC 1: LẤY MENU (Giống hệt User - Vì menu là công khai) ---
    public MenuResponse getGuestMenuData(Integer restaurantId) {
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
            // Lọc theo nhà hàng
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
                dto.setDiscount(p.getDiscount() != null ? p.getDiscount() : 0.0);
                // Xử lý ảnh null
                dto.setImage(p.getImageUrl() != null ? p.getImageUrl() : "/images/default-food.png");
                dto.setCategory(String.valueOf(p.getCategory().getId())); // Quan trọng để lọc tab

                dishList.add(dto);
            }
        }
        response.setMenuItems(dishList);
        return response;
    }

    // --- LOGIC 2: KHÁCH ĐẶT MÓN (Không cần User) ---
    @Transactional
    public void createGuestOrder(GuestOrderRequest request) throws Exception {
        Order order = new Order();

        // --- KHÁC BIỆT CHÍNH SO VỚI USER ---
        order.setUserId(null); // Khách vãng lai không có ID
        // ------------------------------------

        order.setRestaurantId(request.getRestaurantId());
        order.setTableId(request.getTableId());
        order.setNote(request.getNote()); // Nếu GuestOrderRequest có field note thì mở ra
        order.setStatus("PENDING");
        order.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime()
        );

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // Duyệt qua danh sách món khách chọn
        for (GuestOrderRequest.GuestOrderItem itemReq : request.getItems()) {
            Optional<Product> pOpt = productRepo.findById(itemReq.getProductId());

            if (pOpt.isPresent()) {
                Product p = pOpt.get();
                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setProduct(p);
                oi.setQuantity(itemReq.getQuantity());
                BigDecimal unitPrice = p.getPrice(); // Giá gốc

                // Nếu có giảm giá > 0
                if (p.getDiscount() != null && p.getDiscount() > 0) {
                    // Công thức: Giá gốc * (100 - Discount) / 100
                    BigDecimal discountFactor = BigDecimal.valueOf(100 - p.getDiscount()).divide(BigDecimal.valueOf(100));
                    unitPrice = unitPrice.multiply(discountFactor);
                }

                oi.setUnitPrice(unitPrice); // Lưu GIÁ THỰC TẾ (đã giảm) vào DB
                // ============================

                total = total.add(unitPrice.multiply(new BigDecimal(itemReq.getQuantity())));
                orderItems.add(oi);
            }
        }

        if (orderItems.isEmpty()) {
            throw new Exception("Giỏ hàng trống hoặc món ăn không tồn tại");
        }

        order.setTotalAmount(total);
        order.setItems(orderItems);

        orderRepo.save(order);
    }
}