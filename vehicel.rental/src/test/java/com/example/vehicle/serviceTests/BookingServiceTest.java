package com.example.vehicle.serviceTests;

import com.example.vehicle.rental.dto.BookingDTO;
import com.example.vehicle.rental.exception.BookingNotFoundException;
import com.example.vehicle.rental.model.Booking;
import com.example.vehicle.rental.model.PaymentStatus;
import com.example.vehicle.rental.repository.BookingRepository;
import com.example.vehicle.rental.service.BookingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private BookingDTO bookingDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        booking = new Booking();
        booking.setBookingId(1L);
        booking.setUserId(100L);
        booking.setName("John Doe");
        booking.setVehicleId(200L);
        booking.setVehicleName("Honda City");
        booking.setBookingDate(LocalDateTime.now());
        booking.setLocation("New York");
        booking.setAmount(500.0);
        booking.setDuration((long) 3);
        booking.setPaymentStatus(PaymentStatus.SUCCESSFUL);

        bookingDTO = new BookingDTO(booking);
    }

    // ✅ createBooking
    @Test
    void createBooking_ShouldSaveAndReturnBookingDTO() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDTO result = bookingService.createBooking(bookingDTO);

        assertNotNull(result.getBookingId());
        assertEquals("John Doe", result.getName());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // ✅ getAllBookings
    @Test
    void getAllBookings_ShouldReturnListOfBookings() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDTO> result = bookingService.getAllBookings();

        assertEquals(1, result.size());
        assertEquals("Honda City", result.get(0).getVehicleName());
    }

    // ✅ getBookingById
    @Test
    void getBookingById_ShouldReturnBooking_WhenFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDTO result = bookingService.getBookingById(1L);

        assertEquals("John Doe", result.getName());
    }

    @Test
    void getBookingById_ShouldThrow_WhenNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(99L));
    }

    // ✅ updatePaymentStatusByBookingId
    @Test
    void updatePaymentStatus_ShouldUpdateAndReturnDTO() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDTO result = bookingService.updatePaymentStatusByBookingId(1L, PaymentStatus.UNSUCCESSFUL);

        assertEquals(PaymentStatus.UNSUCCESSFUL, result.getPaymentStatus());
    }

    // ✅ deleteBookingById
    @Test
    void deleteBooking_ShouldDelete_WhenExists() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBookingById(1L);

        verify(bookingRepository, times(1)).delete(booking);
    }

    // ✅ getMonthlyBookingAmounts
    @Test
    void getMonthlyBookingAmounts_ShouldReturn12Months() {
        List<Object[]> results = List.of(new Object[]{1, 1000.0}, new Object[]{5, 500.0});
        when(bookingRepository.getMonthlyBookingAmounts()).thenReturn(results);

        Map<Integer, Double> map = bookingService.getMonthlyBookingAmounts();

        assertEquals(12, map.size());
        assertEquals(1000.0, map.get(1));
        assertEquals(500.0, map.get(5));
    }

    // ✅ getPaymentStatusCount
    @Test
    void getPaymentStatusCount_ShouldReturnCounts() {
        when(bookingRepository.countByPaymentStatus(PaymentStatus.SUCCESSFUL)).thenReturn(5L);
        when(bookingRepository.countByPaymentStatus(PaymentStatus.UNSUCCESSFUL)).thenReturn(2L);

        Map<String, Long> result = bookingService.getPaymentStatusCount();

        assertEquals(5L, result.get("SUCCESSFUL"));
        assertEquals(2L, result.get("UNSUCCESSFUL"));
    }

    // ✅ getCurrentMonthSuccessfulAmount
    @Test
    void getCurrentMonthSuccessfulAmount_ShouldReturnValue() {
        when(bookingRepository.getTotalAmountForMonthAndStatus(
                eq(PaymentStatus.SUCCESSFUL),
                anyInt(),
                anyInt()
        )).thenReturn(1200.0);

        Double total = bookingService.getCurrentMonthSuccessfulAmount();

        assertEquals(1200.0, total);
    }

    // ✅ getSuccessfulAmountForYear
    @Test
    void getSuccessfulAmountForYear_ShouldReturn12Entries() {
        when(bookingRepository.sumSuccessfulAmountByMonth(2025, 1)).thenReturn(100.0);
        when(bookingRepository.sumSuccessfulAmountByMonth(2025, 2)).thenReturn(null); // simulate no data

        Map<Integer, Double> result = bookingService.getSuccessfulAmountForYear(2025);

        assertEquals(12, result.size());
        assertEquals(100.0, result.get(1));
        assertEquals(0.0, result.get(2));
    }

    // ✅ setPaymentId
    @Test
    void setPaymentId_ShouldUpdateBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDTO result = bookingService.setPaymentId(1L, 999L);

        assertEquals(999L, result.getPaymentId());
    }

    // ✅ getSuccessfulBookingsByUserId
    @Test
    void getSuccessfulBookingsByUserId_ShouldReturnList_WhenFound() {
        when(bookingRepository.findByUserIdAndPaymentStatus(100L, PaymentStatus.SUCCESSFUL))
                .thenReturn(List.of(booking));

        List<BookingDTO> result = bookingService.getSuccessfulBookingsByUserId(100L);

        assertEquals(1, result.size());
        assertEquals("Honda City", result.get(0).getVehicleName());
    }

    @Test
    void getSuccessfulBookingsByUserId_ShouldThrow_WhenEmpty() {
        when(bookingRepository.findByUserIdAndPaymentStatus(100L, PaymentStatus.SUCCESSFUL))
                .thenReturn(Collections.emptyList());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getSuccessfulBookingsByUserId(100L));
    }

    // ✅ getSuccessfulBookingsBeforeNow
    @Test
    void getSuccessfulBookingsBeforeNow_ShouldReturnList() {
        when(bookingRepository.findByPaymentStatusAndBookingDateBefore(eq(PaymentStatus.SUCCESSFUL), any()))
                .thenReturn(List.of(booking));

        List<BookingDTO> result = bookingService.getSuccessfulBookingsBeforeNow();

        assertEquals(1, result.size());
    }

    @Test
    void getSuccessfulBookingsBeforeNow_ShouldThrow_WhenEmpty() {
        when(bookingRepository.findByPaymentStatusAndBookingDateBefore(eq(PaymentStatus.SUCCESSFUL), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getSuccessfulBookingsBeforeNow());
    }

    // ✅ getVehicleIdsForSuccessfulBookingsBeforeNow
    @Test
    void getVehicleIdsForSuccessfulBookingsBeforeNow_ShouldReturnIds() {
        when(bookingRepository.findVehicleIdsByPaymentStatusAndBookingDateBefore(eq(PaymentStatus.SUCCESSFUL), any()))
                .thenReturn(List.of(200L));

        List<Long> ids = bookingService.getVehicleIdsForSuccessfulBookingsBeforeNow();

        assertEquals(1, ids.size());
        assertEquals(200L, ids.get(0));
    }

    @Test
    void getVehicleIdsForSuccessfulBookingsBeforeNow_ShouldThrow_WhenEmpty() {
        when(bookingRepository.findVehicleIdsByPaymentStatusAndBookingDateBefore(eq(PaymentStatus.SUCCESSFUL), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getVehicleIdsForSuccessfulBookingsBeforeNow());
    }
}
