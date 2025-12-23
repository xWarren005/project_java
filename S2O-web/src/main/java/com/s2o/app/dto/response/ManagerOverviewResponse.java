package com.s2o.app.dto.response;

import lombok.Data; // Sử dụng Lombok cho Getter/Setter
import java.util.List;

@Data
public class ManagerOverviewResponse {
    private StatsGroupDTO stats;
    private RevenueDetailDTO revenueDetail;
    private List<TableDTO> tables;
}