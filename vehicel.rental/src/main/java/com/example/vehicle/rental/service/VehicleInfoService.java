package com.example.vehicle.rental.service;


import com.example.vehicle.rental.dto.ResultDTO;
import com.example.vehicle.rental.dto.VehicleStatusUpdateDto;
import com.example.vehicle.rental.model.PaymentRequest;
import com.example.vehicle.rental.model.User;
import com.example.vehicle.rental.model.VehicleInfo;
import com.example.vehicle.rental.repository.PaymentRequestRepository;
import com.example.vehicle.rental.repository.VehicleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.boot.context.properties.bind.Bindable.mapOf;

@Service
public class VehicleInfoService {

    public VehicleRepository vehicleRepository;
    public PaymentRequestRepository paymentRequestRepository;

    public VehicleInfoService(VehicleRepository vehicleRepository, PaymentRequestRepository paymentRequestRepository) {
        this.vehicleRepository = vehicleRepository;
        this.paymentRequestRepository = paymentRequestRepository;
    }


    public ResponseEntity<?> getVehicleInfo(){

        var vehicle = vehicleRepository.findAllByVehicleAvailability();

        ResultDTO<List<VehicleInfo>> resultDto = new ResultDTO<>();
        resultDto.setData(vehicle);
        resultDto.setStatusCode(200);
        resultDto.setMessage("User saved successfully");
        return ResponseEntity.ok(resultDto);

    }

    public ResponseEntity<?> getVehicle(Long id){

        var payments = paymentRequestRepository.findAllByUserId(id);

        List<Long> vehicleIds = payments.stream()
                .map(PaymentRequest::getVehicleId)
                .collect(Collectors.toList());

        List<VehicleInfo> vehicles = vehicleRepository.findAllById(vehicleIds);


        List<Map<String, Object>> responseList = vehicles.stream().map(v -> {
            // Find corresponding payment for this vehicle
            PaymentRequest payment = payments.stream()
                    .filter(p -> p.getVehicleId().equals(v.getVehicle_id()))
                    .findFirst()
                    .orElse(null);

            BigDecimal additionalCharges = BigDecimal.valueOf(50); // your fixed additional charges
            BigDecimal baseFare = payment.getAmount().subtract(additionalCharges);

// Calculate rental duration in hours
            BigDecimal totalHours = baseFare.divide(
                    v.getVehicle_rate_per_hour(), // rate per hour
                    2,                           // scale: 2 decimal places
                    RoundingMode.HALF_UP          // rounding mode
            );

            Map<String, Object> vehicleMap = new HashMap<>();
            vehicleMap.put("vehicle_name", v.getVehicle_name());
            vehicleMap.put("image_Url", v.getImage_Url() != null ? v.getImage_Url() : "assets/logo.png");
            vehicleMap.put("booking_price", payment.getAmount());
            vehicleMap.put("booking_hours", totalHours);
            vehicleMap.put("vehicle_type", v.getVehicle_type());
            vehicleMap.put("ratings", v.getRatings());
            vehicleMap.put("ratingCount", v.getRatingCount());
            vehicleMap.put("booking_status", payment.getRefundStatus());
            return vehicleMap;
        }).toList();


        ResultDTO<List<Map<String, Object>>> resultDto = new ResultDTO<>();
        resultDto.setData(responseList);
        resultDto.setStatusCode(200);
        resultDto.setMessage("User saved successfully");
        return ResponseEntity.ok(resultDto);

    }
    
    
    
    
    public ResponseEntity<?> addVehicle(VehicleInfo vehicleInfo) {
        try {
            // Set defaults for String fields
            if (vehicleInfo.getVehicle_type() == null) vehicleInfo.setVehicle_type("EMPTY");
            if (vehicleInfo.getVehicle_name() == null) vehicleInfo.setVehicle_name("EMPTY");
            if (vehicleInfo.getImage_Url() == null) vehicleInfo.setImage_Url("EMPTY");
            if (vehicleInfo.getFuel_type() == null) vehicleInfo.setFuel_type("EMPTY");
            if (vehicleInfo.getVehicle_status() == null) vehicleInfo.setVehicle_status("AVAILABLE");

            // Set defaults for numeric fields
            if (vehicleInfo.getVehicle_rate_per_hour() == null) vehicleInfo.setVehicle_rate_per_hour(BigDecimal.ZERO);
            if (vehicleInfo.getVehicle_hourly_rate_late_fee() == null) vehicleInfo.setVehicle_hourly_rate_late_fee(BigDecimal.ZERO);
            if (vehicleInfo.getSeating_capacity() == null) vehicleInfo.setSeating_capacity(0);
            if (vehicleInfo.getRatings() == null) vehicleInfo.setRatings(0);
            if (vehicleInfo.getRatingCount() == null) vehicleInfo.setRatingCount(0);

            // Set defaults for boolean fields
            if (vehicleInfo.getVehicle_availability() == null) vehicleInfo.setVehicle_availability(true);

            // Save to DB
            VehicleInfo saved = vehicleRepository.save(vehicleInfo);

            ResultDTO<VehicleInfo> resultDto = new ResultDTO<>();
            resultDto.setData(saved);
            resultDto.setStatusCode(201);
            resultDto.setMessage("Vehicle added successfully");

            return ResponseEntity.status(201).body(resultDto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    
    public ResponseEntity<?> getVehicleTypeCounts() {
        try {
            List<VehicleInfo> vehicles = vehicleRepository.findAll();

            Map<String, Long> typeCounts = vehicles.stream()
                    .collect(Collectors.groupingBy(
                            v -> v.getVehicle_type() != null ? v.getVehicle_type() : "UNKNOWN",
                            Collectors.counting()
                    ));

            return ResponseEntity.ok(typeCounts);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    public ResponseEntity<?> deleteVehicle(Long vehicleId) {
        try {
            Optional<VehicleInfo> vehicleOpt = vehicleRepository.findById(vehicleId);

            if (vehicleOpt.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Vehicle with ID " + vehicleId + " not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            VehicleInfo vehicle = vehicleOpt.get();

            vehicleRepository.delete(vehicle);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Vehicle with ID " + vehicleId + " deleted successfully");
            response.put("deletedVehicle", vehicle);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    
    public ResponseEntity<?> getVehicleCountByTypeAndName() {
        List<Object[]> results = vehicleRepository.countVehiclesByTypeAndName();

        List<Map<String, Object>> response = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("vehicleType", row[0]);
            map.put("vehicleName", row[1]);
            map.put("count", row[2]);
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }
    
    
    public ResponseEntity<?> getAllVehicles() {
        List<VehicleInfo> vehicles = vehicleRepository.findAll();
        return ResponseEntity.ok(vehicles);
    }
    
    
    
    public ResponseEntity<?> getVehicleTypeAndName(Long vehicleId) {
        Optional<VehicleInfo> vehicleOpt = vehicleRepository.findById(vehicleId);

        if (vehicleOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Vehicle not found with ID: " + vehicleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        VehicleInfo vehicle = vehicleOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("vehicle_id", vehicle.getVehicle_id());
        response.put("vehicle_type", vehicle.getVehicle_type());
        response.put("vehicle_name", vehicle.getVehicle_name());

        return ResponseEntity.ok(response);
    }
    
    
    
    public ResponseEntity<?> updateVehicleStatusAndAvailability(Long vehicleId, VehicleStatusUpdateDto dto) {
        Optional<VehicleInfo> optionalVehicle = vehicleRepository.findById(vehicleId);

        if (optionalVehicle.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Vehicle not found with id " + vehicleId));
        }

        VehicleInfo vehicle = optionalVehicle.get();

        if (dto.getVehicle_status() != null) {
            vehicle.setVehicle_status(dto.getVehicle_status());
        }
        if (dto.getVehicle_availability() != null) {
            vehicle.setVehicle_availability(dto.getVehicle_availability());
        }

        vehicleRepository.save(vehicle);

        return ResponseEntity.ok(Map.of(
                "message", "Vehicle status/availability updated successfully",
                "vehicleId", vehicle.getVehicle_id(),
                "status", vehicle.getVehicle_status(),
                "availability", vehicle.getVehicle_availability()
        ));
    }
    
    
    
    public ResponseEntity<?> getVehicleDetails(Long vehicleId) {
        Optional<VehicleInfo> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isPresent()) {
            return ResponseEntity.ok(vehicleOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Vehicle with ID " + vehicleId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    
    
    










}
