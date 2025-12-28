package com.s2o.app.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MenuResponse {
    private Integer categoryId;
    private String categoryName;
    private List<ProductItem> products;
    @Data
    public static class ProductItem {
        private Integer id;
        private String name;
        private BigDecimal price;
        private Double discount;
        private String imageUrl;
        private String description;
    }
}
