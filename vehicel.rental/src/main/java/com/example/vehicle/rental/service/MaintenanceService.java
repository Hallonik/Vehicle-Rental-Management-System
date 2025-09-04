package com.example.vehicle.rental.service;

import com.example.vehicle.rental.dto.MaintenanceDTO;
import com.example.vehicle.rental.model.Maintenance;
import com.example.vehicle.rental.repository.MaintenanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceService {

    private final MaintenanceRepository repository;

    public MaintenanceService(MaintenanceRepository repository) {
        this.repository = repository;
    }

    public MaintenanceDTO addMaintenance(MaintenanceDTO dto) {
        Maintenance entity = new Maintenance();
        entity.setVehicleId(dto.getVehicleId());
        entity.setVehicleType(dto.getVehicleType());
        entity.setVehicleName(dto.getVehicleName());
        entity.setServiceDate(dto.getServiceDate());
        entity.setCost(dto.getCost());
        entity.setDescription(dto.getDescription());

        Maintenance saved = repository.save(entity);

        dto.setMaintenanceId(saved.getMaintenanceId());
        return dto;
    }

    public List<MaintenanceDTO> getAllMaintenances() {
        return repository.findAll().stream().map(m -> {
            MaintenanceDTO dto = new MaintenanceDTO();
            dto.setMaintenanceId(m.getMaintenanceId());
            dto.setVehicleId(m.getVehicleId());
            dto.setVehicleType(m.getVehicleType());
            dto.setVehicleName(m.getVehicleName());
            dto.setServiceDate(m.getServiceDate());
            dto.setCost(m.getCost());
            dto.setDescription(m.getDescription());
            return dto;
        }).collect(Collectors.toList());
    }
    
    public void deleteMaintenance(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Maintenance record not found with ID: " + id);
        }
        repository.deleteById(id);
    }
        
}
