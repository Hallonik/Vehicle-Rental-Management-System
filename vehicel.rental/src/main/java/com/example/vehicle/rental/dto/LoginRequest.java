package com.example.vehicle.rental.dto;

import com.example.vehicle.rental.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min=8,message="Password should be 8 character long")
    private String password;
    
    @NotNull(message = "Role is required")
    private Role role;  // CUSTOMER or ADMIN
}
