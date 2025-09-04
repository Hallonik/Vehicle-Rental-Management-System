package com.example.vehicle.rental.dto;

import com.example.vehicle.rental.model.Role;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone number must be 10 digits"
    )
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min=8,message="Password should be 8 character long")
    private String password;

    // Optional fields
    @Size(max = 50, message = "Driving license number must be at most 50 characters")
    private String drivingLicenseNumber;

    @Size(max = 50, message = "Payment method must be at most 50 characters")
    private String paymentMethod;

    @NotNull(message = "Role is required")
    private Role role;  // CUSTOMER or ADMIN
    
    @Column(unique = true,nullable = false)
    @NotBlank(message = "Username is required")   
    private String userName;
}
