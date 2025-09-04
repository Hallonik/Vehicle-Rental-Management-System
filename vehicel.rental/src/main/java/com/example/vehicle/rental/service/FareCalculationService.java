package com.example.vehicle.rental.service;

import com.example.vehicle.rental.dto.FareCalculationRequestDTO;
import com.example.vehicle.rental.dto.FareCalculationStoredResultDTO;
import com.example.vehicle.rental.dto.ResultDTO;

public interface FareCalculationService {
    ResultDTO<FareCalculationStoredResultDTO> calculateFare(FareCalculationRequestDTO dto);
}
