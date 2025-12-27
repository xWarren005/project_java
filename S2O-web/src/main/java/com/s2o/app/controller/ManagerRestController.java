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
    // 1. OVERVIEW
    // ==========================================
    @GetMapping("/overview")
    public ResponseEntity<ManagerOverviewResponse> getOverview() {
        Integer currentRestaurantId = 1;
        return ResponseEntity.ok(dashboardService.getDashboardData(currentRestaurantId));
    }

    // ==========================================
    // 2. DISHES (MÓN ĂN)
    // ==========================================
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/dishes")
    public List<Product> getAllDishes() {
        return productRepository.findAll();
    }

    // --- THÊM MÓN ---
    @PostMapping(value = "/dishes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDish(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam Integer categoryId,
            @RequestParam Boolean isAvailable,
            @RequestParam(value = "discount", defaultValue = "0") Double discount, // <--- UPDATE: Nhận discount
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
            product.setDiscount(discount); // <--- UPDATE: Lưu discount
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

    // --- CẬP NHẬT MÓN ---
    @PutMapping(value = "/dishes/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDish(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam Integer categoryId,
            @RequestParam Boolean isAvailable,
            @RequestParam(value = "discount", defaultValue = "0") Double discount, // <--- UPDATE: Nhận discount
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
            product.setDiscount(discount); // <--- UPDATE: Lưu discount

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveImageFile(imageFile, category.getName());
                product.setImageUrl(imageUrl);
            }

            return ResponseEntity.ok(productRepository.save(product));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    @DeleteMapping("/dishes/{id}")
    public ResponseEntity<?> deleteDish(@PathVariable Integer id) {
        if (!productRepository.existsById(id)) return ResponseEntity.notFound().build();
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // 3. TABLES + QR
    // ==========================================
    @GetMapping("/tables")
    public List<TableDTO> getTables() {
        return tableRepository.findByRestaurantId(1)
                .stream()
                .map(TableDTO::fromEntity)
                .collect(Collectors.toList());
    }

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

    // ==========================================
    // 4. UTILS – LƯU ẢNH
    // ==========================================
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