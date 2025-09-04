package com.example.vehicle.rental.controller;

import com.example.vehicle.rental.dto.MaintenanceDTO;
import com.example.vehicle.rental.service.MaintenanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
@CrossOrigin(origins = "*") // allow frontend
public class MaintenanceController {

    private final MaintenanceService service;

    public MaintenanceController(MaintenanceService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<MaintenanceDTO> addMaintenance(@RequestBody MaintenanceDTO dto) {
        return ResponseEntity.ok(service.addMaintenance(dto));
    }

    @GetMapping("/get")
    public ResponseEntity<List<MaintenanceDTO>> getAllMaintenances() {
        return ResponseEntity.ok(service.getAllMaintenances());
    }
    
 // ✅ DELETE Maintenance by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMaintenance(@PathVariable Long id) {
        service.deleteMaintenance(id);
        return ResponseEntity.ok("Maintenance record with ID " + id + " deleted successfully!");
    }
}
