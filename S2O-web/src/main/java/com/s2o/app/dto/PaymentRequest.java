package com.s2o.app.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private int orderId;
    private String paymentMethod; // CASH, BANK_TRANSFER, E_WALLET
    private BigDecimal amountPaid;
    private String transactionRef; // Mã giao dịch (nếu chuyển khoản)

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }
}