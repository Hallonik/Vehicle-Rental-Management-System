package com.example.vehicle.rental.repository;


import com.example.vehicle.rental.model.VehicleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleInfo,Long> {

    @Query("FROM VehicleInfo v WHERE v.vehicle_availability = true AND v.vehicle_status = 'AVAILABLE'")
    List<VehicleInfo> findAllByVehicleAvailability();

    // Count total vehicles except maintenance
    @Query("SELECT COUNT(v) FROM VehicleInfo v")
    Long countAllVehicles();

    // Count available vehicles
    @Query("SELECT COUNT(v) FROM VehicleInfo v WHERE v.vehicle_status = 'AVAILABLE'")
    Long countAvailableVehicles();

    // Count rented vehicles
    @Query("SELECT COUNT(v) FROM VehicleInfo v WHERE v.vehicle_status = 'RENTED'")
    Long countRentedVehicles();
    
    
    @Query("SELECT v.vehicle_type, v.vehicle_name, COUNT(v) " +
    	       "FROM VehicleInfo v " +
    	       "GROUP BY v.vehicle_type, v.vehicle_name")
    	List<Object[]> countVehiclesByTypeAndName();
    	
    	
    	


}
