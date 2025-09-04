package com.example.vehicle.rental.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "baseFare", "tax","totalFare" , "additionalCharges"})
public class FareCalculationStoredResultDTO {
	private double baseFare;
    private double tax;
    private double additionalCharges;
    private double totalFare;

    public FareCalculationStoredResultDTO(double baseFare, double tax,  double totalFare,double additionalCharges) {
        this.baseFare = baseFare;
        this.tax = tax;
        this.totalFare = totalFare;
        this.additionalCharges=additionalCharges;
       
    }

	public double getBaseFare() {
		return baseFare;
	}

	public void setBaseFare(double baseFare) {
		this.baseFare = baseFare;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public double getAdditionalCharges() {
		return additionalCharges;
	}

	public void setAdditionalCharges(double additionalCharges) {
		this.additionalCharges = additionalCharges;
	}

	public double getTotalFare() {
		return totalFare;
	}

	public void setTotalFare(double totalFare) {
		this.totalFare = totalFare;
	}
    

}
