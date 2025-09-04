package com.example.vehicle.rental.repository;

import com.example.vehicle.rental.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionHistory, Long> {
}
