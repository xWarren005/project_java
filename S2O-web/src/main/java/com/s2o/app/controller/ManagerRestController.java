package com.s2o.app.controller;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager")
public class ManagerRestController {

    @Autowired private ManagerDashboardService dashboardService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TableRepository tableRepository;

    @Value("${upload.path}")
    private String uploadRootPath;

    // ==========================================
    // 1. OVERVIEW (TỔNG QUAN)
    // ==========================================

    /**
     * API: Lấy dữ liệu tổng quan cho Dashboard
     * URL: GET /api/manager/overview
     * Chức năng: Trả về doanh thu hôm nay, số bàn đang có khách, số đơn hàng active...
     */
    @GetMapping("/overview")
    public ResponseEntity<ManagerOverviewResponse> getOverview() {
        Integer currentRestaurantId = 1;
        return ResponseEntity.ok(dashboardService.getDashboardData(currentRestaurantId));
    }

    // ==========================================
    // 2. DISHES (QUẢN LÝ MÓN ĂN)
    // ==========================================

    /**
     * API: Lấy danh sách danh mục
     * URL: GET /api/manager/categories
     * Chức năng: Lấy tất cả loại món (Khai vị, Món chính, Đồ uống...) để hiển thị dropdown chọn.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    /**
     * API: Lấy danh sách món ăn
     * URL: GET /api/manager/dishes
     * Chức năng: Hiển thị toàn bộ thực đơn hiện có.
     */
    @GetMapping("/dishes")
    public List<Product> getAllDishes() {
        return productRepository.findAll();
    }

    /**
     * API: Thêm món ăn mới
     * URL: POST /api/manager/dishes
     * Chức năng: Tạo món ăn mới, lưu ảnh vào server, lưu thông tin (giá, giảm giá) vào DB.
     */
    @PostMapping(value = "/dishes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDish(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam Integer categoryId,
            @RequestParam Boolean isAvailable,
            @RequestParam(value = "discount", defaultValue = "0") Double discount, // Nhận discount
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setCategory(category);
            product.setIsAvailable(isAvailable);
            product.setDiscount(discount); // Lưu discount
            product.setAiGenerated(false);

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveImageFile(imageFile, category.getName());
                product.setImageUrl(imageUrl);
            }

            return ResponseEntity.ok(productRepository.save(product));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * API: Cập nhật món ăn
     * URL: PUT /api/manager/dishes/{id}
     * Chức năng: Sửa thông tin món ăn (tên, giá, giảm giá, ảnh...) theo ID.
     */
    @PutMapping(value = "/dishes/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDish(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam Integer categoryId,
            @RequestParam Boolean isAvailable,
            @RequestParam(value = "discount", defaultValue = "0") Double discount, // Nhận discount
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Món ăn không tồn tại"));

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setCategory(category);
            product.setIsAvailable(isAvailable);
            product.setDiscount(discount); // Lưu discount

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveImageFile(imageFile, category.getName());
                product.setImageUrl(imageUrl);
            }

            return ResponseEntity.ok(productRepository.save(product));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    /**
     * API: Xóa món ăn
     * URL: DELETE /api/manager/dishes/{id}
     * Chức năng: Xóa vĩnh viễn món ăn khỏi Database.
     */
    @DeleteMapping("/dishes/{id}")
    public ResponseEntity<?> deleteDish(@PathVariable Integer id) {
        if (!productRepository.existsById(id)) return ResponseEntity.notFound().build();
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // 3. TABLES + QR (QUẢN LÝ BÀN)
    // ==========================================

    /**
     * API: Lấy danh sách bàn
     * URL: GET /api/manager/tables
     * Chức năng: Lấy tất cả bàn để hiển thị sơ đồ bàn.
     */
    @GetMapping("/tables")
    public List<TableDTO> getTables() {
        return tableRepository.findByRestaurantId(1)
                .stream()
                .map(TableDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * API: Tạo bàn mới
     * URL: POST /api/manager/tables
     * Chức năng: Thêm một bàn mới vào hệ thống và tự động tạo link QR code.
     */
    @PostMapping("/tables")
    public ResponseEntity<?> createTable(@RequestBody TableDTO request) {
        try {
            RestaurantTable table = new RestaurantTable();
            table.setRestaurantId(1);
            table.setTableName(request.getName());
            table.setCapacity(request.getSeats());
            table.setStatus(RestaurantTable.TableStatus.AVAILABLE);

            RestaurantTable saved = tableRepository.save(table);
            saved.setQrCodeString("http://localhost:8080/user/menu?tableId=" + saved.getId());
            tableRepository.save(saved);

            return ResponseEntity.ok(TableDTO.fromEntity(saved));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * API: Lấy ảnh QR Code
     * URL: GET /api/manager/tables/{id}/qr
     * Chức năng: Trả về hình ảnh QR Code (dạng byte array - PNG) để hiển thị hoặc tải về.
     */
    @GetMapping(value = "/tables/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Integer id) {
        try {
            RestaurantTable table = tableRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Table not found"));
            String link = table.getQrCodeString();
            if (link == null) link = "http://localhost:8080/user/menu?tableId=" + id;

            return ResponseEntity.ok(QRCodeGenerator.getQRCodeImage(link, 200, 200));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API: Cập nhật trạng thái bàn
     * URL: PUT /api/manager/tables/{id}/status
     * Chức năng: Chuyển trạng thái bàn (0: Trống, 1: Có khách, 2: Đặt trước).
     */
    @PutMapping("/tables/{id}/status")
    public ResponseEntity<?> updateTableStatus(@PathVariable Integer id, @RequestParam Integer status) {
        try {
            RestaurantTable table = tableRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bàn không tồn tại"));
            switch (status) {
                case 0 -> table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
                case 1 -> table.setStatus(RestaurantTable.TableStatus.OCCUPIED);
                case 2 -> table.setStatus(RestaurantTable.TableStatus.RESERVED);
                default -> { return ResponseEntity.badRequest().body("Trạng thái không hợp lệ"); }
            }
            tableRepository.save(table);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * API: Xóa bàn
     * URL: DELETE /api/manager/tables/{id}
     * Chức năng: Xóa bàn khỏi hệ thống.
     */
    @DeleteMapping("/tables/{id}")
    public ResponseEntity<?> deleteTable(@PathVariable Integer id) {
        if (!tableRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            tableRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa bàn: " + e.getMessage());
        }
    }

    // ==========================================
    // 4. UTILS – LƯU ẢNH
    // ==========================================

    /**
     * Hàm nội bộ: Lưu file ảnh upload vào thư mục server
     * Chức năng: Nhận file Multipart, lưu vào thư mục phân loại theo tên danh mục, trả về đường dẫn URL.
     */
    private String saveImageFile(MultipartFile file, String categoryName) throws IOException {

        String folderName = convertToFolderName(categoryName);

        Path uploadDir = Paths.get(uploadRootPath, folderName);
        Files.createDirectories(uploadDir);

        String ext = "";
        if (file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")) {
            ext = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf("."));
        }

        String fileName = System.currentTimeMillis() + ext;
        Path filePath = uploadDir.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println(">>> Saved image: " + filePath.toAbsolutePath());

        return "/uploads/image/" + folderName + "/" + fileName;
    }

    /**
     * Hàm nội bộ: Chuyển tên danh mục thành tên thư mục hợp lệ
     * Ví dụ: "Món Chính" -> "Mon-Chinh"
     */
    private String convertToFolderName(String categoryName) {
        if (categoryName == null) return "other";

        String temp = Normalizer.normalize(categoryName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noSign = pattern.matcher(temp).replaceAll("")
                .replace('đ', 'd').replace('Đ', 'D');

        String clean = noSign.replace(" ", "-");

        if (clean.equalsIgnoreCase("Mon-Chinh")) return "Mon-chinh";
        if (clean.equalsIgnoreCase("Do-Uong")) return "Do-uong";
        if (clean.equalsIgnoreCase("Khai-Vi")) return "Khai-vi";

        return clean;
    }
}