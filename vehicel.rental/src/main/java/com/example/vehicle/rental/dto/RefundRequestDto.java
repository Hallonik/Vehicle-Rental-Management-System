package com.example.vehicle.rental.dto;


import java.math.BigDecimal;

public class RefundRequestDto {
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    private Long paymentId;
    private BigDecimal amount;


    public Long getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
