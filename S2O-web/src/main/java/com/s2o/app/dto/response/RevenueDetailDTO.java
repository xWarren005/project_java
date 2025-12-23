package com.s2o.app.dto.response;

import lombok.Data;

@Data
public class RevenueDetailDTO {
    private String total;
    private double growth;
    private int ordersCount;
    private int invoicesCount;
}