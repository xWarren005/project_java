package com.s2o.app.dto.response;

import lombok.Data;

@Data
public class StatsGroupDTO {
    private StatsDTO totalTables;
    private StatsDTO dishes;
    private StatsDTO orders;
    private StatsDTO revenueToday;
}