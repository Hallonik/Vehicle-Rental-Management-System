package com.example.vehicle.rental.repository;


import com.example.vehicle.rental.model.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest,Long>{
   List<PaymentRequest> findAllByUserId(Long userId);

   // Total revenue from succeeded payments
   @Query("""
    SELECT COALESCE(SUM(p.amount), 0) - COALESCE(SUM(p.refundAmount), 0)
    FROM PaymentRequest p
    WHERE p.status = 'succeeded'
""")
   BigDecimal getTotalRevenue();
   
   @Query("SELECT p.paymentMode, COUNT(p) FROM PaymentRequest p GROUP BY p.paymentMode")
   List<Object[]> getPaymentCountByMode();
   
   Optional<PaymentRequest> findByBookingId(Long bookingId);

}
