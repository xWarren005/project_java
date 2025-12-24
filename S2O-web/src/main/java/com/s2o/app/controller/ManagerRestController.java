package com.s2o.app.controller;

import com.s2o.app.dto.ProductRequest;
import com.s2o.app.dto.response.ManagerOverviewResponse;
import com.s2o.app.entity.Category;
import com.s2o.app.entity.Product;
import com.s2o.app.repository.CategoryRepository;
import com.s2o.app.repository.ProductRepository;
import com.s2o.app.service.ManagerDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerRestController {

    @Autowired
    private ManagerDashboardService dashboardService; // Service cho trang Overview

    @Autowired
    private ProductRepository productRepository;      // Repo cho trang Dishes

    @Autowired
    private CategoryRepository categoryRepository;    // Repo để tìm category khi thêm/sửa món

    // ==========================================
    // 1. API CHO OVERVIEW
    // ==========================================
    @GetMapping("/overview")
    public ResponseEntity<ManagerOverviewResponse> getOverview() {
        // Tạm thời fix cứng ID nhà hàng (Sau này lấy từ Security User)
        Integer currentRestaurantId = 1;
        ManagerOverviewResponse data = dashboardService.getDashboardData(currentRestaurantId);
        return ResponseEntity.ok(data);
    }

    // ==========================================
    // 2. API CHO DISHES (CRUD)
    // ==========================================

    // Lấy danh sách món ăn
    @GetMapping("/dishes")
    public List<Product> getAllDishes() {
        return productRepository.findAll();
    }

    // Thêm món ăn mới (Nhận ProductRequest từ JS)
    @PostMapping("/dishes")
    public ResponseEntity<?> createDish(@RequestBody ProductRequest request) {
        if (request.getCategoryId() == null) {
            return ResponseEntity.badRequest().body("Vui lòng chọn danh mục (Category ID is required)");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại!"));

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setIsAvailable(request.getIsAvailable());
        product.setAiGenerated(false); // Mặc định
        product.setCategory(category); // Gán quan hệ

        return ResponseEntity.ok(productRepository.save(product));
    }

    // Cập nhật món ăn
    @PutMapping("/dishes/{id}")
    public ResponseEntity<?> updateDish(@PathVariable Integer id, @RequestBody ProductRequest request) {
        return productRepository.findById(id).map(product -> {
            product.setName(request.getName());
            product.setPrice(request.getPrice());
            product.setDescription(request.getDescription());
            product.setIsAvailable(request.getIsAvailable());

            if (request.getImageUrl() != null) {
                product.setImageUrl(request.getImageUrl());
            }

            // Nếu người dùng đổi danh mục
            if (request.getCategoryId() != null) {
                Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category không tồn tại!"));
                product.setCategory(category);
            }

            return ResponseEntity.ok(productRepository.save(product));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Xóa món ăn
    @DeleteMapping("/dishes/{id}")
    public ResponseEntity<?> deleteDish(@PathVariable Integer id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok().build(); // Trả về 200 OK
    }
}