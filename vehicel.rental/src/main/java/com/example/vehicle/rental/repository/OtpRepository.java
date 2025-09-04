package com.example.vehicle.rental.repository;

import com.example.vehicle.rental.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmail(String email);              // ✅ Required for AuthService
    Optional<Otp> findByEmailAndOtp(String email, String otp);
    void deleteByEmail(String email);
}
