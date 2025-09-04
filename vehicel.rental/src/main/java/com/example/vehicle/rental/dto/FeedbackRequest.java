package com.example.vehicle.rental.dto;

import java.time.LocalDateTime;

public class FeedbackRequest {
    private Long id;
    private String name;
    private String email;
    private int rating;
    private String message;
    private LocalDateTime createdAt;

    // Constructors
    public FeedbackRequest() {}

    public FeedbackRequest(Long id, String name, String email, int rating, String message, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.rating = rating;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
