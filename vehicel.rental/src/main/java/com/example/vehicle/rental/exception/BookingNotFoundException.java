package com.example.vehicle.rental.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Booking not found with ID: " + id);
    }

	public BookingNotFoundException(String message) {
		super(message);
	}
}
