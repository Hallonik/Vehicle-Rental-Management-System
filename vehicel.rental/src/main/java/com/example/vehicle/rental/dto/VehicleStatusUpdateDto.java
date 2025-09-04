package com.example.vehicle.rental.dto;

public class VehicleStatusUpdateDto {
    private String vehicle_status;
    private Boolean vehicle_availability;

    public String getVehicle_status() {
        return vehicle_status;
    }

    public void setVehicle_status(String vehicle_status) {
        this.vehicle_status = vehicle_status;
    }

    public Boolean getVehicle_availability() {
        return vehicle_availability;
    }

    public void setVehicle_availability(Boolean vehicle_availability) {
        this.vehicle_availability = vehicle_availability;
    }
}
