package com.s2o.app.controller;

import com.s2o.app.dto.BankQrConfigDTO;
import com.s2o.app.dto.response.ManagerOverviewResponse;
import com.s2o.app.dto.response.RevenueDashboardResponse;
import com.s2o.app.dto.response.TableDTO;
import com.s2o.app.entity.Category;
import com.s2o.app.entity.Product;
import com.s2o.app.entity.RestaurantTable;
import com.s2o.app.repository.CategoryRepository;
import com.s2o.app.repository.ProductRepository;
import com.s2o.app.repository.TableRepository;
import com.s2o.app.service.ManagerDashboardService;
import com.s2o.app.service.RestaurantService;
import com.s2o.app.util.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager")
public class ManagerRestController {

    @Autowired private ManagerDashboardService dashboardService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TableRepository tableRepository;
    @Autowired private RestaurantService restaurantService;

    @Value("${upload.path}")
    private String uploadRootPath;

    // ==========================================
    // 1. OVERVIEW (TỔNG QUAN)
    // ==========================================
    @GetMapping("/overview")
    public ResponseEntity<ManagerOverviewResponse> getOverview() {
        Integer currentRestaurantId = 1;
        return ResponseEntity.ok(dashboardService.getDashboardData(currentRestaurantId));
    }

    @GetMapping("/revenue-stats")
    public ResponseEntity<RevenueDashboardResponse> getRevenueStats() {
        Integer currentRestaurantId = 1;
        return ResponseEntity.ok(dashboardService.getRevenueStats(currentRestaurantId));
    }

    // ==========================================
    // 2. DISHES (QUẢN LÝ MÓN ĂN)
    // ==========================================

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/dishes")
    public List<Product> getAllDishes() {
        return productRepository.findAll();
    }

    @PostMapping(value = "/dishes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDish(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam String categoryName, // Nhận tên danh mục (String)
            @RequestParam Boolean isAvailable,
            @RequestParam(value = "discount", defaultValue = "0") Double discount,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            // Tìm hoặc tạo mới Category dựa trên tên
            Category category = getOrCreateCategory(categoryName);

            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setCategory(category);
            product.setIsAvailable(isAvailable);
            product.setDiscount(discount);
            product.setAiGenerated(false);

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveImageFile(imageFile, category.getName());
                product.setImageUrl(imageUrl);
            }

            return ResponseEntity.ok(productRepository.save(product));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }

    @PutMapping(value = "/dishes/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDish(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam String categoryName, // Nhận tên danh mục
            @RequestParam Boolean isAvailable,
            @RequestParam(value = "discount", defaultValue = "0") Double discount,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Món ăn không tồn tại"));

            // Tìm hoặc tạo mới Category
            Category category = getOrCreateCategory(categoryName);

            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setCategory(category);
            product.setIsAvailable(isAvailable);
            product.setDiscount(discount);

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
    // 3. TABLES (QUẢN LÝ BÀN)
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
            // Tạo link QR (đây là QR bàn ăn, khác với QR ngân hàng)
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
    // 4. QR CONFIGURATION (CẤU HÌNH NGÂN HÀNG)
    // ==========================================

    @GetMapping("/qr-config")
    public ResponseEntity<BankQrConfigDTO> getQrConfig() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(restaurantService.getQrConfig(username));
    }

    @PostMapping("/qr-config")
    public ResponseEntity<String> saveQrConfig(@RequestBody BankQrConfigDTO configDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        restaurantService.updateQrConfig(username, configDTO);
        return ResponseEntity.ok("Cập nhật mã QR thành công!");
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private Category getOrCreateCategory(String name) {
        List<Category> all = categoryRepository.findAll();
        Optional<Category> existing = all.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        } else {
            Category newCat = new Category();
            newCat.setName(name);
            newCat.setRestaurantId(1);
            newCat.setDisplayOrder(all.size() + 1);
            return categoryRepository.save(newCat);
        }
    }

    private String saveImageFile(MultipartFile file, String categoryName) throws IOException {
        String folderName = convertToFolderName(categoryName);

        Path uploadDir = Paths.get(uploadRootPath, folderName);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

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

        String clean = noSign.trim().replace(" ", "-");

        if (clean.equalsIgnoreCase("Mon-chinh")) return "Mon-chinh";
        if (clean.equalsIgnoreCase("Do-uong")) return "Do-uong";
        if (clean.equalsIgnoreCase("Khai-vi")) return "Khai-vi";
        if (clean.equalsIgnoreCase("Trang-mieng")) return "Trang-Mieng";

        return clean;
    }
}