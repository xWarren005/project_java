package com.s2o.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueDashboardResponse {
    private List<SummaryStatDTO> summary;
    private List<ChartDataDTO> chartData;
    private List<TopDishDTO> top5Dishes;
}
