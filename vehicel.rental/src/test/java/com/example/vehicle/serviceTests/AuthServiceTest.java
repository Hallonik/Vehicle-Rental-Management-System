package com.example.vehicle.serviceTests;

import com.example.vehicle.rental.dto.LoginRequest;
import com.example.vehicle.rental.dto.RegisterRequest;
import com.example.vehicle.rental.dto.UpdateProfileRequest;
import com.example.vehicle.rental.dto.UserProfileResponse;
import com.example.vehicle.rental.exception.*;
import com.example.vehicle.rental.model.*;
import com.example.vehicle.rental.repository.OtpRepository;
import com.example.vehicle.rental.repository.UserRepository;
import com.example.vehicle.rental.security.JwtUtil;
import com.example.vehicle.rental.service.AuthService;
import com.example.vehicle.rental.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OtpRepository otpRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = RegisterRequest.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .phone("1234567890")
                .password("password123")
                .drivingLicenseNumber("DL123")
                .paymentMethod("CARD")
                .role(Role.CUSTOMER)
                .userName("johnny")
                .build();

        testUser = User.builder()
                .userId(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .phone("1234567890")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .isEmailVerified(true)
                .userName("johnny")
                .build();
    }

    // ✅ Register
    @Test
    void register_ShouldSendOtp_WhenNewUser() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);

        String response = authService.register(registerRequest);

        assertTrue(response.contains("OTP sent"));
        verify(emailService, times(1)).sendOtp(eq("john@example.com"), anyString());
        verify(otpRepository, times(1)).save(any(Otp.class));
    }

    @Test
    void register_ShouldThrow_WhenEmailExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> authService.register(registerRequest));
    }

    // ✅ Verify OTP
    @Test
    void verifyOtp_ShouldRegisterUser_WhenOtpValid() {
        Otp otp = Otp.builder()
                .email("john@example.com")
                .otp("123456")
                .createdAt(LocalDateTime.now())
                .build();

        authService.register(registerRequest); // store in pendingUsers
        when(otpRepository.findByEmail("john@example.com")).thenReturn(Optional.of(otp));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        String response = authService.verifyOtp("john@example.com", "123456");

        assertEquals("User registered successfully after OTP verification.", response);
        verify(userRepository, times(1)).save(any(User.class));
        verify(otpRepository, times(1)).delete(otp);
    }

    @Test
    void verifyOtp_ShouldThrow_WhenOtpInvalid() {
        Otp otp = Otp.builder()
                .email("john@example.com")
                .otp("111111")
                .createdAt(LocalDateTime.now())
                .build();

        authService.register(registerRequest);
        when(otpRepository.findByEmail("john@example.com")).thenReturn(Optional.of(otp));

        assertThrows(InvalidOtpException.class,
                () -> authService.verifyOtp("john@example.com", "123456"));
    }

    // ✅ Login
    @Test
    void login_ShouldReturnJwt_WhenValidCredentials() {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password123", Role.CUSTOMER);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        String token = authService.login(loginRequest);

        assertEquals("jwt-token", token);
    }

    @Test
    void login_ShouldThrow_WhenRoleMismatch() {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password123", Role.ADMIN);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));
    }

    // ✅ Forgot Password
    @Test
    void forgotPassword_ShouldSendOtp() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        String result = authService.forgotPassword("john@example.com");

        assertTrue(result.contains("OTP sent"));
        verify(emailService, times(1)).sendPasswordResetOtp(eq("john@example.com"), anyString());
    }

    // ✅ Verify Forgot Password OTP
    @Test
    void verifyForgotPasswordOtp_ShouldReturnToken_WhenOtpValid() {
        Otp otp = Otp.builder()
                .email("john@example.com")
                .otp("123456")
                .createdAt(LocalDateTime.now())
                .build();

        when(otpRepository.findByEmail("john@example.com")).thenReturn(Optional.of(otp));
        when(jwtUtil.generateForgotPasswordToken("john@example.com")).thenReturn("reset-token");

        String token = authService.verifyForgotPasswordOtp("john@example.com", "123456");

        assertEquals("reset-token", token);
    }

    // ✅ Reset Password
    @Test
    void resetPassword_ShouldUpdatePassword_WhenTokenValid() {
        when(jwtUtil.extractUsername("reset-token")).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        String result = authService.resetPassword("reset-token", "newPass", "newPass");

        assertEquals("Password reset successfully.", result);
        verify(userRepository, times(1)).save(testUser);
    }

    // ✅ Update Profile
    @Test
    void updateProfile_ShouldUpdateUser() {
        UpdateProfileRequest req = new UpdateProfileRequest("Jane Doe", "9876543210", "DL999", "PAYPAL");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        String response = authService.updateProfile("john@example.com", req);

        assertEquals("Profile updated successfully", response);
        assertEquals("Jane Doe", testUser.getFullName());
    }

    // ✅ Get Profile
    @Test
    void getProfile_ShouldReturnUserProfile() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        UserProfileResponse profile = authService.getProfile("john@example.com");

        assertEquals("John Doe", profile.getFullName());
        assertEquals(Role.CUSTOMER, profile.getRole());
    }
}
