package com.example.vehicle.rental.model;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "transaction_history")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
    }

    public Long getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(Long canceledAt) {
        this.canceledAt = canceledAt;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    private BigDecimal amountReceived;

    private Long canceledAt;

    private  String paymentIntentId;
}
