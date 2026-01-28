package com.s2o.app.dto;

import java.math.BigDecimal;

public class PaymentRequest {

    /* =====================
       MODE 1: THANH TOÁN 1 ORDER
       ===================== */
    private Integer orderId;        // Dùng khi thanh toán đơn
    private BigDecimal amountPaid;  // Tiền khách đưa (CASH)

    /* =====================
       MODE 2: THANH TOÁN THEO BÀN
       ===================== */
    private Integer tableId;        // Dùng khi thanh toán gộp

    /* =====================
       CHUNG
       ===================== */
    private String paymentMethod;   // CASH, BANK_TRANSFER, E_WALLET
    private String transactionRef;  // Mã giao dịch

    /* =====================
       GETTERS & SETTERS
       ===================== */
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    /* =====================
       HELPER METHODS (OPTIONAL)
       ===================== */
    public boolean isOrderPayment() {
        return orderId != null;
    }

    public boolean isTablePayment() {
        return tableId != null;
    }
}
