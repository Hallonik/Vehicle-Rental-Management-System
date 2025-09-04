package com.example.vehicle.rental.controller;


import com.example.vehicle.rental.dto.PaymentRequestDto;
import com.example.vehicle.rental.dto.VehicleStatusUpdateDto;
import com.example.vehicle.rental.model.VehicleInfo;
import com.example.vehicle.rental.service.PaymentProcessService;
import com.example.vehicle.rental.service.VehicleInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleInfoController {

    public VehicleInfoService vehicleInfoService;


    public VehicleInfoController(VehicleInfoService vehicleInfoService) {
        this.vehicleInfoService = vehicleInfoService;
    }
    
    
    
    @GetMapping(
            path = "/allVehicles",
            produces = "application/json")
    public ResponseEntity<?> getAllVehicles() {
        try {
            return vehicleInfoService.getAllVehicles();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    

    @GetMapping(
            path = "/available",
            produces =  "application/json")
    public ResponseEntity<?> createPaymentIntent() {
        try {

            return vehicleInfoService.getVehicleInfo();

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
        }
    }

    @GetMapping(
            path = "/bookedByUser/{id}",
            produces =  "application/json")
    public ResponseEntity<?> getVehiclesBookedByUser(@PathVariable("id") Long userId) {
        try {

            return vehicleInfoService.getVehicle(userId);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
        }
    }
    
    
    @PostMapping(
            path = "/add",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<?> addVehicle(@RequestBody VehicleInfo vehicleInfo) {
        return vehicleInfoService.addVehicle(vehicleInfo);
    }
    
    
    @GetMapping(
            path = "/typecount",
            produces = "application/json")
    public ResponseEntity<?> getVehicleTypeCounts() {
        try {
            return vehicleInfoService.getVehicleTypeCounts();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    
    @DeleteMapping(
            path = "/deleteVehicle/{id}",
            produces = "application/json")
    public ResponseEntity<?> deleteVehicle(@PathVariable("id") Long vehicleId) {
        try {
            return vehicleInfoService.deleteVehicle(vehicleId);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    
    @GetMapping("/countByTypeAndName")
    public ResponseEntity<?> getVehicleCountByTypeAndName() {
        try {
        	
        	System.out.println("HelloWorld");
        	return vehicleInfoService.getVehicleCountByTypeAndName();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    
    @GetMapping(
            path = "/{id}/type-name",
            produces = "application/json")
    public ResponseEntity<?> getVehicleTypeAndName(@PathVariable("id") Long vehicleId) {
        try {
            return vehicleInfoService.getVehicleTypeAndName(vehicleId);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PatchMapping(
            path = "/updateStatus/{id}",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<?> updateVehicleStatusAndAvailability(
            @PathVariable("id") Long vehicleId,
            @RequestBody VehicleStatusUpdateDto dto) {
        try {
            return vehicleInfoService.updateVehicleStatusAndAvailability(vehicleId, dto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping(
            path = "/details/{id}",
            produces = "application/json")
    public ResponseEntity<?> getVehicleDetails(@PathVariable("id") Long vehicleId) {
        try {
            return vehicleInfoService.getVehicleDetails(vehicleId);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    
   






}
