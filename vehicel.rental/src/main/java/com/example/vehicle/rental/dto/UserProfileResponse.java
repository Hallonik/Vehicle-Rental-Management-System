package com.example.vehicle.rental.dto;

import com.example.vehicle.rental.model.Role;
import com.example.vehicle.rental.model.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
	 private Long userId; 
    private String fullName;
    private String email;
    private String phone;
    private String drivingLicenseNumber;
    private String paymentMethod;
    private String userName;
    private UserStatus status;
    private Role role;
    
}
