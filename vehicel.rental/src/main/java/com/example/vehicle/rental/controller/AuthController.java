package com.example.vehicle.rental.controller;

import com.example.vehicle.rental.dto.ForgotPasswordRequest;
import com.example.vehicle.rental.dto.LoginRequest;
import com.example.vehicle.rental.dto.RegisterRequest;
import com.example.vehicle.rental.dto.ResetPasswordRequest;
import com.example.vehicle.rental.dto.UpdateProfileRequest;
import com.example.vehicle.rental.dto.UserProfileResponse;
import com.example.vehicle.rental.dto.VerifyForgotPasswordOtpRequest;
import com.example.vehicle.rental.security.JwtUtil;
import com.example.vehicle.rental.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
	
	@Autowired
	private final JwtUtil jwtUtil;
	
	@Autowired
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String message = authService.register(request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Not Registered: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body("Email and OTP are required.");
        }

        try {
            String result = authService.verifyOtp(email, otp);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("OTP Verification Failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            return ResponseEntity.ok("Login Successful. Token: " + token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Login Unsuccessful: " + e.getMessage());
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request.getEmail()));
    }

    @PostMapping("/verify-forgot-otp")
    public ResponseEntity<String> verifyForgotOtp(@Valid @RequestBody VerifyForgotPasswordOtpRequest request) {
        return ResponseEntity.ok(authService.verifyForgotPasswordOtp(request.getEmail(), request.getOtp()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ResetPasswordRequest request) {

        // Extract the token (remove "Bearer " prefix)
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        String message = authService.resetPassword(
                token, request.getNewPassword(), request.getConfirmPassword());

        return ResponseEntity.ok(message);
    }

    
   

    @PutMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(
    		@Valid @RequestBody UpdateProfileRequest request,
            @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractUsername(token.substring(7)); // use instance, not static
        String message = authService.updateProfile(email, request);
        return ResponseEntity.ok(message);
    }
    
    
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractUsername(token.substring(7));
        UserProfileResponse profile = authService.getProfile(email);
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/allusers")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    
    @PostMapping("/search")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(@RequestBody Map<String, String> request) {
        String keyword = request.get("keyword"); // frontend will send { "keyword": "Imran" }
        List<UserProfileResponse> results = authService.searchUsers(keyword);
        return ResponseEntity.ok(results);
    }
    
    
    @DeleteMapping("/deleteuser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            String message = authService.deleteUser(userId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Failed to delete user: " + e.getMessage());
        }
    }
    
    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        String result = authService.toggleStatus(id);
        return ResponseEntity.ok(result);
    }
    
    
 // ✅ Get user status
    @GetMapping("/users/getstatus/{id}")
    public ResponseEntity<String> getUserStatus(@PathVariable Long id) {
        try {
            String status = authService.getUserStatus(id);
            return ResponseEntity.ok("User status: " + status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Failed to fetch user status: " + e.getMessage());
        }
    }

    // ✅ Set user status (ACTIVE / BLOCKED)
    @PutMapping("/users/setstatus/{id}")
    public ResponseEntity<String> setUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String newStatus = request.get("status"); // expected values: "ACTIVE" or "BLOCKED"
        if (newStatus == null) {
            return ResponseEntity.badRequest().body("Status is required.");
        }

        try {
            String result = authService.setUserStatus(id, newStatus);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Failed to update status: " + e.getMessage());
        }
    }
    
    
    
 // Inside AuthController.java

    @GetMapping("/user/{userName}")
    public ResponseEntity<Map<String, Object>> getUserByUserName(@PathVariable String userName) {
        try {
            UserProfileResponse user = authService.getUserByUserName(userName);

            // Extract only userId and fullName
            Map<String, Object> response = Map.of(
                    "userId", user.getUserId(),
                    "fullName", user.getFullName()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    







}
