package com.example.vehicle.serviceTests;

import com.example.vehicle.rental.dto.MaintenanceDTO;
import com.example.vehicle.rental.model.Maintenance;
import com.example.vehicle.rental.repository.MaintenanceRepository;
import com.example.vehicle.rental.service.MaintenanceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceTest {

    @Mock
    private MaintenanceRepository repository;

    @InjectMocks
    private MaintenanceService service;

    private Maintenance maintenance;
    private MaintenanceDTO dto;

    @BeforeEach
    void setUp() {
        maintenance = new Maintenance();
        maintenance.setMaintenanceId(1L);
        maintenance.setVehicleId(101L);
        maintenance.setVehicleType("Car");
        maintenance.setVehicleName("Toyota");
        maintenance.setServiceDate(LocalDate.of(2025, 8, 29));
        maintenance.setCost(2500.0);
        maintenance.setDescription("Oil change");

        dto = new MaintenanceDTO();
        dto.setVehicleId(101L);
        dto.setVehicleType("Car");
        dto.setVehicleName("Toyota");
        dto.setServiceDate(LocalDate.of(2025, 8, 29));
        dto.setCost(2500.0);
        dto.setDescription("Oil change");
    }

    @Test
    void testAddMaintenance() {
        when(repository.save(any(Maintenance.class))).thenReturn(maintenance);

        MaintenanceDTO result = service.addMaintenance(dto);

        assertNotNull(result);
        assertEquals(1L, result.getMaintenanceId());
        assertEquals("Toyota", result.getVehicleName());
        verify(repository, times(1)).save(any(Maintenance.class));
    }

    @Test
    void testGetAllMaintenances() {
        when(repository.findAll()).thenReturn(Arrays.asList(maintenance));

        List<MaintenanceDTO> result = service.getAllMaintenances();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getVehicleName());
        assertEquals(2500.0, result.get(0).getCost());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testDeleteMaintenance_Success() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteMaintenance(1L);

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMaintenance_NotFound() {
        when(repository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.deleteMaintenance(1L));

        assertEquals("Maintenance record not found with ID: 1", exception.getMessage());
        verify(repository, times(1)).existsById(1L);
        verify(repository, never()).deleteById(anyLong());
    }
}
