package com.s2o.app.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Double discount;
    private Boolean isAvailable;
    private Integer categoryId;
}