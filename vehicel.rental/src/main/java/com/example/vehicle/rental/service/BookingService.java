package com.example.vehicle.rental.service;

import com.example.vehicle.rental.dto.BookingDTO;
import com.example.vehicle.rental.exception.BookingNotFoundException;
import com.example.vehicle.rental.model.Booking;
import com.example.vehicle.rental.model.PaymentStatus;
import com.example.vehicle.rental.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public BookingDTO createBooking(BookingDTO dto) {
        try {
            Booking booking = new Booking();
            booking.setUserId(dto.getUserId());
            booking.setName(dto.getName());
            booking.setVehicleId(dto.getVehicleId());
            booking.setVehicleName(dto.getVehicleName());
            booking.setBookingDate(LocalDateTime.now());
            booking.setLocation(dto.getLocation());
            booking.setAmount(dto.getAmount());
            booking.setDuration(dto.getDuration());      
            booking.setPaymentStatus(dto.getPaymentStatus());

            Booking saved = bookingRepository.save(booking);

            dto.setBookingId(saved.getBookingId());
            dto.setBookingDate(saved.getBookingDate());
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create booking. Please try again later.");
        }
    }

    public List<BookingDTO> getAllBookings() {
        try {
            return bookingRepository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch bookings.");
        }
    }

    public BookingDTO getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    private BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setUserId(booking.getUserId());
        dto.setName(booking.getName());
        dto.setVehicleId(booking.getVehicleId());
        dto.setVehicleName(booking.getVehicleName());
        dto.setBookingDate(booking.getBookingDate());
        dto.setLocation(booking.getLocation());
        dto.setAmount(booking.getAmount());
        dto.setDuration(booking.getDuration());  // ✅ Correct mapping
        dto.setPaymentStatus(booking.getPaymentStatus());
        return dto;
    }

    
    
    public BookingDTO updatePaymentStatusByBookingId(Long bookingId, PaymentStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.setPaymentStatus(status);
        bookingRepository.save(booking);

        return new BookingDTO(booking);
    }
    
    
    public void deleteBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        bookingRepository.delete(booking);
    }
    
    
    public Map<Integer, Double> getMonthlyBookingAmounts() {
        List<Object[]> results = bookingRepository.getMonthlyBookingAmounts();
        Map<Integer, Double> monthlyTotals = new LinkedHashMap<>();

        // always initialize all 12 months with 0
        for (int i = 1; i <= 12; i++) {
            monthlyTotals.put(i, 0.0);
        }

        for (Object[] row : results) {
            Integer month = ((Number) row[0]).intValue();
            Double total = ((Number) row[1]).doubleValue();
            monthlyTotals.put(month, total);
        }

        return monthlyTotals;
    }
    
    
    public Map<String, Long> getPaymentStatusCount() {
        Long successfulCount = bookingRepository.countByPaymentStatus(PaymentStatus.SUCCESSFUL);
        Long unsuccessfulCount = bookingRepository.countByPaymentStatus(PaymentStatus.UNSUCCESSFUL);

        Map<String, Long> result = new HashMap<>();
        result.put("SUCCESSFUL", successfulCount);
        result.put("UNSUCCESSFUL", unsuccessfulCount);

        return result;
    }
    
    
   
    
    public Double getCurrentMonthSuccessfulAmount() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        return bookingRepository.getTotalAmountForMonthAndStatus(PaymentStatus.SUCCESSFUL, month, year);
    }
    
    
    public Map<Integer, Double> getSuccessfulAmountForYear(int year) {
        Map<Integer, Double> result = new LinkedHashMap<>(); // keeps months in order

        for (int month = 1; month <= 12; month++) {
            Double total = bookingRepository.sumSuccessfulAmountByMonth(year, month);
            if (total == null) total = 0.0;
            result.put(month, total);
        }

        return result;
    }
    
    
    
    public BookingDTO setPaymentId(Long bookingId, Long paymentId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.setPaymentId(paymentId);
        Booking saved = bookingRepository.save(booking);

        return new BookingDTO(saved);  // use constructor directly
    }
    
    
    
    public List<BookingDTO> getSuccessfulBookingsByUserId(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserIdAndPaymentStatus(userId, PaymentStatus.SUCCESSFUL);

        if (bookings == null || bookings.isEmpty()) {
            throw new BookingNotFoundException("No successful bookings found for userId " + userId);
        }

        return bookings.stream()
                       .map(BookingDTO::new) // use your DTO constructor
                       .toList();
    }
    
    
    public List<BookingDTO> getSuccessfulBookingsBeforeNow() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingRepository.findByPaymentStatusAndBookingDateBefore(
                    PaymentStatus.SUCCESSFUL, now);

            if (bookings == null || bookings.isEmpty()) {
                throw new BookingNotFoundException("No successful bookings found before current date");
            }

            return bookings.stream()
                           .map(BookingDTO::new)
                           .toList();

        } catch (BookingNotFoundException ex) {
            // rethrow custom exception so controller can return 404
            throw ex;
        } catch (Exception ex) {
            // wrap other unexpected exceptions
            throw new RuntimeException("Failed to fetch successful bookings before now: " + ex.getMessage(), ex);
        }
    }
    
    
    
    public List<Long> getVehicleIdsForSuccessfulBookingsBeforeNow() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfToday = today.atStartOfDay(); // 00:00 today

            List<Long> vehicleIds = bookingRepository.findVehicleIdsByPaymentStatusAndBookingDateBefore(
                    PaymentStatus.SUCCESSFUL, startOfToday);

            if (vehicleIds == null || vehicleIds.isEmpty()) {
                throw new BookingNotFoundException("No vehicleIds found for successful bookings before today");
            }

            return vehicleIds;

        } catch (BookingNotFoundException ex) {
            throw ex; // controller can map this to 404
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch vehicleIds: " + ex.getMessage(), ex);
        }
    }







}
