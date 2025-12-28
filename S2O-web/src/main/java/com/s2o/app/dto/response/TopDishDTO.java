package com.s2o.app.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TopDishDTO {

    private Integer id;
    private String name;
    private Long count;
    private BigDecimal total;
    private String img;

    public TopDishDTO(Integer id, String name, Long count, Number total, String img) {
        this.id = id;
        this.name = name;
        this.count = count != null ? count : 0L;
        this.total = total != null ? new BigDecimal(total.toString()) : BigDecimal.ZERO;
        this.img = img;
    }
}
