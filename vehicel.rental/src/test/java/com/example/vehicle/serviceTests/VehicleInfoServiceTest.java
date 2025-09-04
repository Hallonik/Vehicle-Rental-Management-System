package com.example.vehicle.serviceTests;

import com.example.vehicle.rental.dto.ResultDTO;
import com.example.vehicle.rental.dto.VehicleStatusUpdateDto;
import com.example.vehicle.rental.model.PaymentRequest;
import com.example.vehicle.rental.model.VehicleInfo;
import com.example.vehicle.rental.repository.PaymentRequestRepository;
import com.example.vehicle.rental.repository.VehicleRepository;
import com.example.vehicle.rental.service.VehicleInfoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleInfoServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PaymentRequestRepository paymentRequestRepository;

    @InjectMocks
    private VehicleInfoService service;

    private VehicleInfo vehicle;
    private PaymentRequest payment;

    @BeforeEach
    void setUp() {
        vehicle = new VehicleInfo();
        vehicle.setVehicle_id(1L);
        vehicle.setVehicle_type("Car");
        vehicle.setVehicle_name("Toyota");
        vehicle.setVehicle_rate_per_hour(BigDecimal.valueOf(100));
        vehicle.setRatings(5);
        vehicle.setRatingCount(10);
        vehicle.setVehicle_availability(true);

        payment = new PaymentRequest();
        payment.setVehicleId(1L);
        payment.setAmount(BigDecimal.valueOf(600));
        payment.setRefundStatus("CONFIRMED");
    }

    @Test
    void testGetVehicleInfo() {
        when(vehicleRepository.findAllByVehicleAvailability()).thenReturn(List.of(vehicle));

        ResponseEntity<?> response = service.getVehicleInfo();

        assertEquals(200, ((ResultDTO<?>) response.getBody()).getStatusCode());
        verify(vehicleRepository, times(1)).findAllByVehicleAvailability();
    }

    @Test
    void testGetVehicle() {
        when(paymentRequestRepository.findAllByUserId(1L)).thenReturn(List.of(payment));
        when(vehicleRepository.findAllById(List.of(1L))).thenReturn(List.of(vehicle));

        ResponseEntity<?> response = service.getVehicle(1L);

        assertEquals(200, ((ResultDTO<?>) response.getBody()).getStatusCode());
        List<?> data = ((ResultDTO<List<?>>) response.getBody()).getData();
        assertEquals("Toyota", ((Map<?, ?>) data.get(0)).get("vehicle_name"));
    }

    @Test
    void testAddVehicle_WithDefaults() {
        VehicleInfo input = new VehicleInfo(); // all null
        when(vehicleRepository.save(any(VehicleInfo.class))).thenAnswer(invocation -> {
            VehicleInfo v = invocation.getArgument(0);
            v.setVehicle_id(99L);
            return v;
        });

        ResponseEntity<?> response = service.addVehicle(input);

        assertEquals(201, ((ResultDTO<?>) response.getBody()).getStatusCode());
        VehicleInfo saved = ((ResultDTO<VehicleInfo>) response.getBody()).getData();
        assertEquals("EMPTY", saved.getVehicle_name());
    }

    @Test
    void testGetVehicleTypeCounts() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        ResponseEntity<?> response = service.getVehicleTypeCounts();

        Map<?, ?> map = (Map<?, ?>) response.getBody();
        assertEquals(1L, map.get("Car"));
    }

    @Test
    void testDeleteVehicle_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        ResponseEntity<?> response = service.deleteVehicle(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void testDeleteVehicle_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = service.deleteVehicle(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

  

    @Test
    void testGetAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        ResponseEntity<?> response = service.getAllVehicles();

        List<?> list = (List<?>) response.getBody();
        assertEquals("Toyota", ((VehicleInfo) list.get(0)).getVehicle_name());
    }

    @Test
    void testGetVehicleTypeAndName_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        ResponseEntity<?> response = service.getVehicleTypeAndName(1L);

        Map<?, ?> map = (Map<?, ?>) response.getBody();
        assertEquals("Car", map.get("vehicle_type"));
    }

    @Test
    void testGetVehicleTypeAndName_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = service.getVehicleTypeAndName(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateVehicleStatusAndAvailability_Success() {
        VehicleStatusUpdateDto dto = new VehicleStatusUpdateDto();
        dto.setVehicle_status("IN_USE");
        dto.setVehicle_availability(false);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(VehicleInfo.class))).thenReturn(vehicle);

        ResponseEntity<?> response = service.updateVehicleStatusAndAvailability(1L, dto);

        Map<?, ?> map = (Map<?, ?>) response.getBody();
        assertEquals("IN_USE", map.get("status"));
        assertEquals(false, map.get("availability"));
    }

    @Test
    void testUpdateVehicleStatusAndAvailability_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = service.updateVehicleStatusAndAvailability(1L, new VehicleStatusUpdateDto());

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetVehicleDetails_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        ResponseEntity<?> response = service.getVehicleDetails(1L);

        assertEquals("Toyota", ((VehicleInfo) response.getBody()).getVehicle_name());
    }

    @Test
    void testGetVehicleDetails_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = service.getVehicleDetails(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
