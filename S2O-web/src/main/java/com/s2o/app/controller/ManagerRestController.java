package com.s2o.app.controller;

import com.s2o.app.dto.ProductRequest;
import com.s2o.app.dto.response.ManagerOverviewResponse;
import com.s2o.app.dto.response.TableDTO;
import com.s2o.app.entity.Category;
import com.s2o.app.entity.Product;
import com.s2o.app.entity.RestaurantTable;
import com.s2o.app.repository.CategoryRepository;
import com.s2o.app.repository.ProductRepository;
import com.s2o.app.repository.TableRepository;
import com.s2o.app.service.ManagerDashboardService;
import com.s2o.app.util.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager")
public class ManagerRestController {

    @Autowired
    private ManagerDashboardService dashboardService; // Service cho trang Overview

    @Autowired
    private ProductRepository productRepository;      // Repo cho trang Dishes

    @Autowired
    private CategoryRepository categoryRepository;    // Repo để tìm category khi thêm/sửa món

    @Autowired
    private TableRepository tableRepository;          // Repo cho trang Tables

    // ==========================================
    // 1. API CHO TRANG OVERVIEW (THỐNG KÊ)
    // ==========================================
    @GetMapping("/overview")
    public ResponseEntity<ManagerOverviewResponse> getOverview() {
        // Tạm thời fix cứng ID nhà hàng (Sau này lấy từ Security User)
        Integer currentRestaurantId = 1;
        ManagerOverviewResponse data = dashboardService.getDashboardData(currentRestaurantId);
        return ResponseEntity.ok(data);
    }

    // ==========================================
    // 2. API CHO TRANG DISHES (QUẢN LÝ MÓN ĂN)
    // ==========================================

    // Lấy danh sách món ăn
    @GetMapping("/dishes")
    public List<Product> getAllDishes() {
        return productRepository.findAll();
    }

    // Thêm món ăn mới
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
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // 3. API TABLES (QUẢN LÝ BÀN & QR)
    // ==========================================

    // API 3.1: Lấy danh sách bàn
    @GetMapping("/tables")
    public List<TableDTO> getTables() {
        return tableRepository.findByRestaurantId(1).stream()
                .map(TableDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // API 3.2: Tạo bàn mới + Tự động sinh link QR lưu vào DB
    @PostMapping("/tables")
    public ResponseEntity<?> createTable(@RequestBody TableDTO request) {
        RestaurantTable table = new RestaurantTable();
        table.setRestaurantId(1);
        table.setTableName(request.getName());
        table.setCapacity(request.getSeats());
        table.setStatus(RestaurantTable.TableStatus.AVAILABLE);

        // B1: Lưu lần đầu để có ID
        RestaurantTable savedTable = tableRepository.save(table);

        // B2: Tạo đường link (Lưu ý: Thay localhost bằng IP máy bạn nếu test điện thoại)
        String qrLink = "http://localhost:8080/user/menu?tableId=" + savedTable.getId();

        // B3: Update link vào DB
        savedTable.setQrCodeString(qrLink);
        tableRepository.save(savedTable);

        return ResponseEntity.ok(TableDTO.fromEntity(savedTable));
    }

    // API 3.3: Trả về HÌNH ẢNH QR Code (Front-end dùng thẻ img src=API này)
    @GetMapping(value = "/tables/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Integer id) {
        try {
            RestaurantTable table = tableRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Table not found"));

            String link = table.getQrCodeString();
            if (link == null) link = "http://localhost:8080/user/menu?tableId=" + id;

            // Vẽ ảnh 200x200 pixel
            return ResponseEntity.ok(QRCodeGenerator.getQRCodeImage(link, 200, 200));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // API 3.4: Cập nhật trạng thái bàn (SỬA LỖI MAPPING ENUM)
    // URL: PUT /api/manager/tables/{id}/status?status=1
    @PutMapping("/tables/{id}/status")
    public ResponseEntity<?> updateTableStatus(@PathVariable Integer id, @RequestParam Integer status) {
        try {
            RestaurantTable table = tableRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bàn không tồn tại"));

            // LOGIC QUAN TRỌNG: Chuyển đổi Số (Frontend) -> Enum (Database)
            switch (status) {
                case 0:
                    table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
                    break;
                case 1:
                    table.setStatus(RestaurantTable.TableStatus.OCCUPIED);
                    break;
                case 2:
                    table.setStatus(RestaurantTable.TableStatus.RESERVED);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Trạng thái không hợp lệ (chỉ nhận 0, 1, 2)");
            }

            tableRepository.save(table);
            return ResponseEntity.ok("Cập nhật thành công");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }
}