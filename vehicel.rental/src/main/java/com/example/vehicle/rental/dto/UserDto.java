package com.example.vehicle.rental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long userId;  // keep ID if you need it for updates

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;   // matches User.fullName

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;      // matches User.email

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;      // matches User.phone

    private String role;       // will hold Role enum as string ("ADMIN", "CUSTOMER" etc.)

    private String userName;   // matches User.userName (login username)

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String password;   // matches User.password
}
