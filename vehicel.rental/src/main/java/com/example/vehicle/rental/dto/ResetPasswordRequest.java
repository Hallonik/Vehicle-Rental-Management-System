package com.example.vehicle.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

   
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min=8,message="Password should be 8 character long")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
