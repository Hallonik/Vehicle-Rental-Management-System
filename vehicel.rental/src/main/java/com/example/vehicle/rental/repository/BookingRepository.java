package com.example.vehicle.rental.repository;

import com.example.vehicle.rental.model.Booking;
import com.example.vehicle.rental.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	
	@Query("SELECT MONTH(b.bookingDate), COUNT(b.bookingDate) " +
		       "FROM Booking b " +
		       "WHERE YEAR(b.bookingDate) = YEAR(CURRENT_DATE) " +
		       "GROUP BY MONTH(b.bookingDate)")
		List<Object[]> getMonthlyBookingAmounts();
		
		
		Long countByPaymentStatus(PaymentStatus status);
		
		
		
		
		  @Query("SELECT COALESCE(SUM(b.amount), 0) " +
		           "FROM Booking b " +
		           "WHERE b.paymentStatus = :status " +
		           "AND MONTH(b.bookingDate) = :month " +
		           "AND YEAR(b.bookingDate) = :year")
		    Double getTotalAmountForMonthAndStatus(@Param("status") PaymentStatus status,
		                                           @Param("month") int month,
		                                           @Param("year") int year);
		  
		  @Query("SELECT SUM(b.amount) FROM Booking b " +
			       "WHERE b.paymentStatus = 'SUCCESSFUL' " +
			       "AND YEAR(b.bookingDate) = :year " +
			       "AND MONTH(b.bookingDate) = :month")
			Double sumSuccessfulAmountByMonth(@Param("year") int year, @Param("month") int month);
		  
		  
		  List<Booking> findByUserIdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);

		  
		  
		  List<Booking> findByPaymentStatusAndBookingDateBefore(
		            PaymentStatus status, LocalDateTime dateTime);
		  
		  
		  @Query("SELECT b.vehicleId FROM Booking b " +
			       "WHERE b.paymentStatus = :status " +
			       "AND b.bookingDate < :startOfToday")
			List<Long> findVehicleIdsByPaymentStatusAndBookingDateBefore(
			        @Param("status") PaymentStatus status,
			        @Param("startOfToday") LocalDateTime startOfToday);


		  
}
