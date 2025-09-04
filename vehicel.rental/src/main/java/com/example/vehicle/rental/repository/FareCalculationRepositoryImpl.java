package com.example.vehicle.rental.repository;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FareCalculationRepositoryImpl implements fareRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> callFareCalculationProcedure(int vehicleId, String vehicleName, String vehicleType, int rentalDuration) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("calculate_fare")
                .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN);

        query.setParameter(1, vehicleId);
        query.setParameter(2, vehicleName);
        query.setParameter(3, vehicleType);
        query.setParameter(4, rentalDuration);

        return query.getResultList();
        
    }
}