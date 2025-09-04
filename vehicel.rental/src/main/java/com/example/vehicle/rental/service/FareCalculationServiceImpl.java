package com.example.vehicle.rental.service;

import com.example.vehicle.rental.dto.FareCalculationRequestDTO;
import com.example.vehicle.rental.dto.FareCalculationStoredResultDTO;
import com.example.vehicle.rental.dto.ResultDTO;
import com.example.vehicle.rental.repository.fareRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FareCalculationServiceImpl implements FareCalculationService {

    private final fareRepository repository;

    public FareCalculationServiceImpl(fareRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResultDTO<FareCalculationStoredResultDTO> calculateFare(FareCalculationRequestDTO dto) {
        List<Object[]> result = repository.callFareCalculationProcedure(
                dto.getVehicleId(),
                dto.getVehicleName(),
                dto.getVehicleType(),
                dto.getRentalDuration()
        );

        if (result.isEmpty()) {
            return ResultDTO.error("No fare data returned", 500);
        }

        Object[] row = result.get(0);

        // Check if result is an error message
        if (row.length == 1 && row[0] instanceof String) {
            return ResultDTO.error(row[0].toString(), 404);
        }

        // Parse and map stored procedure output
        double baseFare = ((Number) row[0]).doubleValue();
        double tax = ((Number) row[1]).doubleValue();

        double totalFare = ((Number) row[2]).doubleValue();
        double additionalCharges = ((Number) row[3]).doubleValue();

        FareCalculationStoredResultDTO response = new FareCalculationStoredResultDTO(
                baseFare,
                tax,
                totalFare,
                additionalCharges
               
        );

        return ResultDTO.success(response);
    }


}
