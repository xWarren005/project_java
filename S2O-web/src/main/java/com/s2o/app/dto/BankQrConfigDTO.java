package com.s2o.app.dto;

import lombok.Data;

@Data
public class BankQrConfigDTO {
    private String bankId;      // VD: VCB
    private String accountNo;   // VD: 1234567890
    private String accountName; // VD: NGUYEN VAN A
    private String template;    // VD: compact2
    private String content;     // VD: Thanh toan hoa don
}