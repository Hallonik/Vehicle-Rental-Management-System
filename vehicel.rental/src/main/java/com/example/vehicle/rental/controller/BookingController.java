package com.example.vehicle.rental.controller;

import com.example.vehicle.rental.dto.BookingDTO;
import com.example.vehicle.rental.exception.BookingNotFoundException;
import com.example.vehicle.rental.model.PaymentStatus;
import com.example.vehicle.rental.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO dto) {
        return ResponseEntity.ok(bookingService.createBooking(dto));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        BookingDTO booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }
    
    @PatchMapping("/updatePaymentStatus/{bookingId}")
    public ResponseEntity<BookingDTO> updatePaymentStatusById(
            @PathVariable Long bookingId,
            @RequestParam("status") PaymentStatus status) {
    	
    	 System.out.println(">>> Inside updatePaymentStatusByBookingId, bookingId=" + bookingId + ", status=" + status);

        BookingDTO updatedBooking = bookingService.updatePaymentStatusByBookingId(bookingId, status);
        return ResponseEntity.ok(updatedBooking);
    }
    
    
    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<Map<String, Object>> deleteBooking(@PathVariable Long bookingId) {
        Map<String, Object> response = new HashMap<>();
        try {
        	
        	System.out.println("The BookingId is"+bookingId);
            bookingService.deleteBookingById(bookingId);

            response.put("success", true);
            response.put("message", "Booking deleted successfully");
            response.put("bookingId", bookingId);

            return ResponseEntity.ok(response);
        } catch (BookingNotFoundException ex) {
            response.put("success", false);
            response.put("message", "Booking not found with ID " + bookingId);

            return ResponseEntity.status(404).body(response);
        }
        
       
    }
    
    @GetMapping("/monthly")
    public Map<Integer, Double> getMonthlyBookingAmounts() {
        return bookingService.getMonthlyBookingAmounts();
    }

    
    @GetMapping("/statusCount")
    public ResponseEntity<Map<String, Long>> getPaymentStatusCount() {
        Map<String, Long> response = bookingService.getPaymentStatusCount();
        return ResponseEntity.ok(response);
    }
    
    
    
    
    
    @GetMapping("/successful/amount/currentMonth")
    public ResponseEntity<Map<String, Object>> getSuccessfulBookingAmountForCurrentMonth() {
        Double totalAmount = bookingService.getCurrentMonthSuccessfulAmount();

        Map<String, Object> response = new HashMap<>();
        response.put("month", LocalDate.now().getMonth().toString());
        response.put("year", LocalDate.now().getYear());
        response.put("totalSuccessfulAmount", totalAmount);

        return ResponseEntity.ok(response);
    }
    
    
    @GetMapping("/successful/amount/yearly")
    public ResponseEntity<?> getSuccessfulBookingAmountForYear() {
        int currentYear = LocalDate.now().getYear();
        Map<Integer, Double> monthlyTotals = bookingService.getSuccessfulAmountForYear(currentYear);

        // Check if all values are 0.0
        boolean hasData = monthlyTotals.values().stream().anyMatch(val -> val > 0);

        if (!hasData) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "No successful bookings found for year " + currentYear);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(monthlyTotals);
    }
    
    
    @PatchMapping("/setPaymentId/{bookingId}")
    public ResponseEntity<BookingDTO> setPaymentId(
            @PathVariable Long bookingId,
            @RequestParam("paymentId") Long paymentId) {

        System.out.println(">>> Inside setPaymentId, bookingId=" + bookingId + ", paymentId=" + paymentId);

        BookingDTO updatedBooking = bookingService.setPaymentId(bookingId, paymentId);
        return ResponseEntity.ok(updatedBooking);
    }
    
    
    
    @GetMapping("/user/{userId}/successful")
    public ResponseEntity<?> getSuccessfulBookingsByUserId(@PathVariable Long userId) {
        try {
            System.out.println(">>> Inside getSuccessfulBookingsByUserId, userId=" + userId);

            List<BookingDTO> successfulBookings = bookingService.getSuccessfulBookingsByUserId(userId);

            if (successfulBookings.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No successful bookings found for userId " + userId);
                return ResponseEntity.status(404).body(response);
            }

            // If you also want total count + total amount:
            double totalAmount = successfulBookings.stream()
                                                   .mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0)
                                                   .sum();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("totalSuccessfulBookings", successfulBookings.size());
            response.put("totalAmount", totalAmount);
            response.put("bookings", successfulBookings);

            return ResponseEntity.ok(response);

        } catch (BookingNotFoundException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(404).body(response);

        } catch (Exception ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    
    
    @GetMapping("/successful/beforeNow")
    public ResponseEntity<?> getSuccessfulBookingsBeforeNow() {
        try {
            List<BookingDTO> bookings = bookingService.getSuccessfulBookingsBeforeNow();

//            double totalAmount = bookings.stream()
//                                         .mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0)
//                                         .sum();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalBookings", bookings.size());
//            response.put("totalAmount", totalAmount);
            response.put("bookings", bookings);

            return ResponseEntity.ok(response);

        } catch (BookingNotFoundException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(404).body(response);

        } catch (Exception ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    
    
    @GetMapping("/successful/beforeNow/vehicleIds")
    public ResponseEntity<?> getVehicleIdsForSuccessfulBookingsBeforeNow() {
        try {
            List<Long> vehicleIds = bookingService.getVehicleIdsForSuccessfulBookingsBeforeNow();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", vehicleIds.size());
            response.put("vehicleIds", vehicleIds);

            return ResponseEntity.ok(response);

        } catch (BookingNotFoundException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(404).body(response);

        } catch (Exception ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }






    
    



}
