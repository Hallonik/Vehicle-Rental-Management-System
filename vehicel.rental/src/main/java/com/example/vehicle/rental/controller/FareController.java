package com.example.vehicle.rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vehicle.rental.dto.FareCalculationRequestDTO;
import com.example.vehicle.rental.dto.FareCalculationStoredResultDTO;
import com.example.vehicle.rental.dto.ResultDTO;
import com.example.vehicle.rental.service.FareCalculationService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/Fare")
@Tag(name = "FareController", description = "Endpoints for fare calculation and related operations")
public class FareController {
	@Autowired
	private final FareCalculationService service;

    public FareController(FareCalculationService service) {
        this.service = service;
    }

    @PostMapping(
            path = "/FareCalculation",
            consumes =  "application/json",
            produces =  "application/json"
    )
    public ResponseEntity<ResultDTO<FareCalculationStoredResultDTO>> calculateFare(
            @RequestBody FareCalculationRequestDTO dto) {
        return ResponseEntity.ok(service.calculateFare(dto));
    }
}


