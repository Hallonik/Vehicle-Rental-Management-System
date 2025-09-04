package com.example.vehicle.rental.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicleinfo")
public class VehicleInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicle_id;

    
    private String vehicle_type;

   
    private String vehicle_name;

    public String getImage_Url() {
        return image_Url;
    }

    public void setImage_Url(String image_Url) {
        this.image_Url = image_Url;
    }

    private String image_Url;


    public String getVehicle_status() {
        return vehicle_status;
    }

    public void setVehicle_status(String vehicle_status) {
        this.vehicle_status = vehicle_status;
    }

    @Column(nullable = false)
    private String vehicle_status = "AVAILABLE";

    
    private BigDecimal vehicle_rate_per_hour;
    
    
    public BigDecimal getVehicle_hourly_rate_late_fee() {
        return vehicle_hourly_rate_late_fee;
    }

    public void setVehicle_hourly_rate_late_fee(BigDecimal vehicle_hourly_rate_late_fee) {
        this.vehicle_hourly_rate_late_fee = vehicle_hourly_rate_late_fee;
    }

    public Long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(Long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public BigDecimal getVehicle_rate_per_hour() {
        return vehicle_rate_per_hour;
    }

    public void setVehicle_rate_per_hour(BigDecimal vehicle_rate_per_hour) {
        this.vehicle_rate_per_hour = vehicle_rate_per_hour;
    }

    public Boolean getVehicle_availability() {
        return vehicle_availability;
    }

    public void setVehicle_availability(Boolean vehicle_availability) {
        this.vehicle_availability = vehicle_availability;
    }

    
    private BigDecimal vehicle_hourly_rate_late_fee;

    @Column(nullable = false)
    private Boolean vehicle_availability = true;

    public Integer getSeating_capacity() {
        return seating_capacity;
    }

    public void setSeating_capacity(Integer seating_capacity) {
        this.seating_capacity = seating_capacity;
    }

  
    private Integer seating_capacity;

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public Integer getRatings() {
        return ratings;
    }

    public void setRatings(Integer ratings) {
        this.ratings = ratings;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    
    private String fuel_type;
    private Integer ratings=0;
    private Integer ratingCount=0;
    
    

}
