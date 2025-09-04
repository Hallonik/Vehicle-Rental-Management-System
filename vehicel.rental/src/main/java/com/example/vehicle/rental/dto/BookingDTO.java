package com.example.vehicle.rental.dto;

import com.example.vehicle.rental.model.Booking;
import com.example.vehicle.rental.model.PaymentStatus;

import java.time.LocalDateTime;

public class BookingDTO {
    private Long bookingId;
    private Long userId;
    private String name;
    private Long vehicleId;
    private String vehicleName;
    private LocalDateTime bookingDate;
    private String location;
    private Double amount;
  	private Long duration;
    private PaymentStatus paymentStatus;
    private Long paymentId;
    
    
    

	// Default constructor
    public BookingDTO() {}

    // Constructor to map from entity
    public BookingDTO(Booking booking) {
        this.bookingId = booking.getBookingId();
        this.userId = booking.getUserId();
        this.name = booking.getName();
        this.vehicleId = booking.getVehicleId();
        this.vehicleName = booking.getVehicleName();
        this.bookingDate = booking.getBookingDate();
        this.location = booking.getLocation();
        this.amount = booking.getAmount();
        this.duration = booking.getDuration();
        this.paymentStatus = booking.getPaymentStatus();
        this.paymentId=booking.getPaymentId();
    }
    
    


    // Getters and Setters
    
    
    public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}
}
