package com.s2o.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableDTO {
    private Integer id;
    private int status; // 0: Trống, 1: Có khách, 2: Đặt trước
    private String name;
}