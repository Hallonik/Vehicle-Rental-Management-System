package com.example.vehicle.rental.dto;

public class FareCalculationRequestDTO {
	private int vehicleId;
    private String vehicleName;
    private String vehicleType;
    private int rentalDuration;

	public FareCalculationRequestDTO(int vehicleId, String vehicleName, String vehicleType, int rentalDuration) {
		super();
		this.vehicleId = vehicleId;
		this.vehicleName = vehicleName;
		this.vehicleType = vehicleType;
		this.rentalDuration = rentalDuration;
	}
	public int getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getVehicleName() {
		return vehicleName;
	}
	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public int getRentalDuration() {
		return rentalDuration;
	}
	public void setRentalDuration(int rentalDuration) {
		this.rentalDuration = rentalDuration;
	}
	
    
    

}
