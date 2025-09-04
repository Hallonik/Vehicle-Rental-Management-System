package com.example.vehicle.rental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Entity
@Table(name = "payment")
public class PaymentRequest {

  
    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private BigDecimal amount;        // amount in cents
    private String currency;
   
    private String name;           // name on card
   
    private String email;
   
    private String paymentMode;    // e.g. card, upi, netbanking
    private String paymentFor;
    private String remark;
    private String paymentIntentId;
    private String status;
    private BigDecimal security_charges;
    private Long bookingId;          // to store bookingId
    
    
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }


    public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public BigDecimal getSecurity_charges() {
		return security_charges;
	}

	public void setSecurity_charges(BigDecimal security_charges) {
		this.security_charges = security_charges;
	}

	public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    private BigDecimal refundAmount;
    private String refundStatus;

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    private BigDecimal remainingAmount;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "user_id")
    private Long userId;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentFor() {
        return paymentFor;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    @Column(name = "vehicle_id")
    private Long vehicleId;

    public PaymentRequest() {}

    public PaymentRequest(BigDecimal amount, String currency, String name, String email, Long userId,
                             String paymentMode, String paymentFor, String remark,String paymentIntentId,String status ,Long bookingId) {
        this.amount = amount;
        this.currency = currency;
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.paymentMode = paymentMode;
        this.paymentFor = paymentFor;
        this.remark = remark;
        this.paymentIntentId = paymentIntentId;
        this.status = status;
        this.bookingId=bookingId;
    }

    // Getters and Setters

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRemainingAmount(BigDecimal amount) {
        this.remainingAmount = amount;
    }

    public void setRefundAmount(BigDecimal amount) {
        this.refundAmount = amount;
    }


    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }


    public void setPaymentFor(String paymentFor) {
        this.paymentFor = paymentFor;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

