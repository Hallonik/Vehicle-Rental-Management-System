package com.example.vehicle.rental.dto;


import java.math.BigDecimal;

public class PaymentRequestDto {

    private BigDecimal amount;        // amount in cents

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String currency;
    private String name;           // name on card


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;           // name on card

    public String getEmail() {
        return email;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public String getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentFor() {
        return paymentFor;
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

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    private String email;
    private String paymentMode;    // e.g. card, upi, netbanking
    private String paymentFor;
    private String remark;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	private Long vehicleId;
    private BigDecimal refundAmount;
    
    private Long bookingId; 

}
