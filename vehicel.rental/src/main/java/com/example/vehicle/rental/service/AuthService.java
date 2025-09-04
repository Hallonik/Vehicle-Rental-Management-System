package com.example.vehicle.rental.service;

import com.example.vehicle.rental.dto.RegisterRequest;
import com.example.vehicle.rental.dto.UpdateProfileRequest;
import com.example.vehicle.rental.dto.UserProfileResponse;
import com.example.vehicle.rental.dto.LoginRequest;
import com.example.vehicle.rental.model.User;
import com.example.vehicle.rental.model.UserStatus;
import com.example.vehicle.rental.model.Otp;
import com.example.vehicle.rental.model.Role;
import com.example.vehicle.rental.exception.*;
import com.example.vehicle.rental.repository.UserRepository;
import com.example.vehicle.rental.repository.OtpRepository;
import com.example.vehicle.rental.security.JwtUtil;
import com.example.vehicle.rental.util.Contants;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;

import io.jsonwebtoken.lang.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    // Temporary in-memory store for pending user registrations
    private final Map<String, RegisterRequest> pendingUsers = new HashMap<>();

    // Step 1: Register user and send OTP
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already in use.");
        }

        pendingUsers.put(request.getEmail(), request);

        String otp = generateOtp();

        Otp otpEntity = Otp.builder()
                .email(request.getEmail())
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .build();
        otpRepository.save(otpEntity);

        emailService.sendOtp(request.getEmail(), otp);

        return "OTP sent to " + request.getEmail() + ". Please verify to complete registration.";
    }

    // Step 2: Verify OTP and save user
    public String verifyOtp(String email, String otp)  {
    	
        Otp storedOtp = otpRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidOtpException("No OTP found for this email."));

        if (!storedOtp.getOtp().equals(otp)) {
            throw new InvalidOtpException("Invalid OTP.");
        }

        if (storedOtp.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("OTP has expired.");
        }

        RegisterRequest request = pendingUsers.get(email);
        if (request == null) {
            throw new ResourceNotFoundException("No pending registration found for this email.");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .drivingLicenseNumber(request.getDrivingLicenseNumber())
                .paymentMethod(request.getPaymentMethod())
                .role(request.getRole())
                .isEmailVerified(true)
                .userName(request.getUserName())
                .build();
        
        try {

        // ✅ Stripe integration: only if role == CUSTOMER
        if (request.getRole() == Role.CUSTOMER) {
            // Set Stripe API key (should ideally be set once globally, e.g., in a config class)
            Stripe.apiKey = Contants.SECRET_KEY;

            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setName(request.getFullName())
                    .setEmail(request.getEmail())
                    .build();

            Customer customer = Customer.create(params);
            user.setStripe_customer_id(customer.getId());
        }

        userRepository.save(user);
        } catch (StripeException e) {
            throw new RuntimeException("Stripe customer creation failed: " + e.getMessage(), e);
        }

        otpRepository.delete(storedOtp);
        pendingUsers.remove(email);

        return "User registered successfully after OTP verification.";
    	
    }


    // Step 3: Login
    public String login(LoginRequest request) {
    	
    	System.out.println(request.getEmail()+" "+request.getPassword()+" "+request.getRole());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        
        System.out.println("Stored User Role"+user.getRole());
        
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UnauthorizedException("User not valid");
        }
        
        if (!user.getRole().equals(request.getRole())) {
            throw new BadCredentialsException("Invalid role for this account");
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    // Step 4: Forgot password - send OTP
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email not found"));

        String otp = generateOtp();

        Otp otpEntity = otpRepository.findByEmail(email)
                .map(existing -> {
                    existing.setOtp(otp);
                    existing.setCreatedAt(LocalDateTime.now());
                    return existing;
                })
                .orElse(Otp.builder()
                        .email(email)
                        .otp(otp)
                        .createdAt(LocalDateTime.now())
                        .build());

        otpRepository.save(otpEntity);

        emailService.sendPasswordResetOtp(email, otp);

        return "OTP sent to your email to reset password.";
    }

 // Step 5: Verify OTP for forgot password
    public String verifyForgotPasswordOtp(String email, String otp) {
        Otp storedOtp = otpRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidOtpException("No OTP found for this email."));

        if (!storedOtp.getOtp().equals(otp)) {
            throw new InvalidOtpException("Invalid OTP.");
        }

        if (storedOtp.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("OTP has expired.");
        }

        // ✅ Generate a short-lived JWT token for password reset
        String token = jwtUtil.generateForgotPasswordToken(email);

        // Optional: remove OTP from database
        otpRepository.delete(storedOtp);

        return token; // Send this token to frontend to use in resetPassword
    }


 // Step 6: Reset password using token
    public String resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException("Passwords do not match.");
        }

        // ✅ Extract email from JWT token
        String email;
        try {
            email = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            throw new InvalidOtpException("Invalid or expired token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password reset successfully.";
    }
    
 // Utility method to generate a 6-digit OTP
    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
    
    
   // Step7: Update Profile details
    public String updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setDrivingLicenseNumber(request.getDrivingLicenseNumber());
        user.setPaymentMethod(request.getPaymentMethod());

        userRepository.save(user);
        return "Profile updated successfully";
    }
    
    
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return new UserProfileResponse(
        		user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getDrivingLicenseNumber(),
                user.getPaymentMethod(),
                user.getUsername(),
                user.getStatus(),
                user.getRole()
                
        );
    }
    
    public List<UserProfileResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserProfileResponse(
                		user.getUserId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getDrivingLicenseNumber(),
                        user.getPaymentMethod(),
                        user.getUsername(),     // username matches DTO field
                        user.getStatus(),       // pass enum directly
                        user.getRole()          // pass enum directly
                ))
                .toList();
    }
    
    
    public List<UserProfileResponse> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword)
                .stream()
                .map(user -> new UserProfileResponse(
                		user.getUserId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getDrivingLicenseNumber(),
                        user.getPaymentMethod(),
                        user.getUsername(),
                        user.getStatus(),   // enum goes directly
                        user.getRole()      // enum goes directly
                ))
                .toList();
    }
    
    
    public String deleteUser(Long userId) {
        com.example.vehicle.rental.model.User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        userRepository.delete(user);
        return "User deleted successfully with ID: " + userId;
    }
    
    
    
    
    public String toggleStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.BLOCKED);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }

        userRepository.save(user);

        return "User " + user.getFullName() + " is now " + user.getStatus();
    }
    
    
    public String getUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return user.getStatus().name();  // returns "ACTIVE" or "BLOCKED"
    }

    public String setUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        try {
            UserStatus newStatus = UserStatus.valueOf(status.toUpperCase()); // ACTIVE / BLOCKED
            user.setStatus(newStatus);
            userRepository.save(user);
            return "User status updated to " + newStatus.name();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status. Use ACTIVE or BLOCKED.");
        }
    }
    
    
    public UserProfileResponse getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + userName));

        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .drivingLicenseNumber(user.getDrivingLicenseNumber())
                .paymentMethod(user.getPaymentMethod())
                .userName(user.getUsername())
                .status(user.getStatus())   // ✅ directly assign enum
                .role(user.getRole())       // ✅ directly assign enum
                .build();
    }




    
    
   




}
