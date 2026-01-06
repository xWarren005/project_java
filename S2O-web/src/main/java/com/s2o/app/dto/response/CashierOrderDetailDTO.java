package com.s2o.app.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class CashierOrderDetailDTO {
    private Integer orderId;
    private String tableName;
    private BigDecimal totalAmount;
    private List<DetailItem> items;

    // [MỚI] Thêm field chứa cấu hình ngân hàng
    private BankQrConfigDTO bankConfig;

    // Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<DetailItem> getItems() {
        return items;
    }

    public void setItems(List<DetailItem> items) {
        this.items = items;
    }

    public BankQrConfigDTO getBankConfig() {
        return bankConfig;
    }

    public void setBankConfig(BankQrConfigDTO bankConfig) {
        this.bankConfig = bankConfig;
    }

    // Inner Class cho item món ăn
    public static class DetailItem {
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }
    }

    // [MỚI] Inner Class cho cấu hình ngân hàng (để map từ JSON DB)
    public static class BankQrConfigDTO {
        private String bankId;
        private String accountNo;
        private String template;
        private String accountName;

        public String getBankId() {
            return bankId;
        }

        public void setBankId(String bankId) {
            this.bankId = bankId;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public void setAccountNo(String accountNo) {
            this.accountNo = accountNo;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }
    }
}