package com.s2o.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SummaryStatDTO {
    private String title;
    private String value;
    private String sub;
    private String icon;
}
