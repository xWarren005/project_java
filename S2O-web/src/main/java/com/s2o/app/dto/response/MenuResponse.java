package com.s2o.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MenuResponse {
    // Danh sách danh mục (để hiển thị Tab)
    private List<CategoryDTO> categories;

    // Danh sách tất cả món ăn (để hiển thị Grid)
    private List<DishDTO> menuItems;
    @Data
    public static class CategoryDTO {
        private String id;
        private String name;
    }
    @Data
    public static class DishDTO {
        private String id;
        private String name;
        private String description;
        private BigDecimal price;
        private String image;
        private String category; // ID danh mục để lọc
    }
}
