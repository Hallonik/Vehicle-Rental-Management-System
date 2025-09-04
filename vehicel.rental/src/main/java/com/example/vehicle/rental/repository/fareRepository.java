package com.example.vehicle.rental.repository;
import java.util.List;

public interface fareRepository {
    List<Object[]> callFareCalculationProcedure(int vehicleId, String vehicleName, String vehicleType, int rentalDuration);

}
