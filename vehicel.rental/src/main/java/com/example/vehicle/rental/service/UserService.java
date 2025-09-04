package com.example.vehicle.rental.service;


import com.example.vehicle.rental.dto.CredDTO;
import com.example.vehicle.rental.dto.ResultDTO;
import com.example.vehicle.rental.dto.UserDto;
import com.example.vehicle.rental.model.Role;
import com.example.vehicle.rental.model.User;
import com.example.vehicle.rental.repository.PaymentRequestRepository;
import com.example.vehicle.rental.repository.UserRepository;
import com.example.vehicle.rental.repository.VehicleRepository;
import com.example.vehicle.rental.security.JwtService;
import com.example.vehicle.rental.util.Contants;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    public UserRepository userRepository;
    public JwtService jwtService;
    public PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final VehicleRepository vehicleRepository;
    private final PaymentRequestRepository paymentRequestRepository;


    public UserService(UserRepository userRepository,PaymentRequestRepository paymentRequestRepository,VehicleRepository vehicleRepository, AuthenticationManager authenticationManager,JwtService jwtService,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.vehicleRepository = vehicleRepository;
        this.paymentRequestRepository = paymentRequestRepository;
    }

    public ResponseEntity<?> addUser(UserDto user) throws StripeException {

        if (user == null || user.getRole() == null) {
            return ResponseEntity.badRequest().body("Invalid user input");
        }

        System.out.println("hsbsv"+ user.getRole());

        var userExist = userRepository.getUserByEmail(user.getEmail());

        if (userExist != null){
            return ResponseEntity.badRequest().body("email Id already exists");
        }

        User newUser = new User();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone()); // fixed: phone instead of mobileNumber
        newUser.setRole(Role.valueOf(user.getRole())); // convert String → Enum
        newUser.setUsername(user.getUserName()); // fixed: setUserName instead of setUsername
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));


        if (Objects.equals(user.getRole(), Contants.RoleCheck.CUSTOMER.getRole())) {
            // Ideally, this should be configured once at app startup
            Stripe.apiKey = Contants.SECRET_KEY;

            CustomerCreateParams params = CustomerCreateParams.builder()
            		.setName(user.getFullName())
                    .setEmail(user.getEmail())
                    .build();

            Customer customer = Customer.create(params);
            newUser.setStripe_customer_id(customer.getId());

            var result = userRepository.save(newUser);

            ResultDTO<User> resultDto = new ResultDTO<>();
            resultDto.setData(result);
            resultDto.setStatusCode(200);
            resultDto.setMessage("User saved successfully with customer role");

            return ResponseEntity.ok(resultDto);

        } else if (Objects.equals(user.getRole(), Contants.RoleCheck.ADMIN.getRole())) {

            System.out.println("jjkvkj");
           userRepository.save(newUser);

            ResultDTO<User> resultDto = new ResultDTO<>();
            resultDto.setData(newUser);
            resultDto.setStatusCode(200);
            resultDto.setMessage("User saved successfully");
            return ResponseEntity.ok(resultDto);

        } else {
            return ResponseEntity.badRequest().body("Unsupported user role");
        }
    }

    public ResponseEntity<?> authenticate( CredDTO credDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credDto.getUserName(),
                            credDto.getPassword()
                    )
            );


            User user = userRepository.findByUserName(credDto.getUserName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));


            String token = jwtService.generateToken(user);

            Map<String, Object> json = new HashMap<>();
            json.put("token", token);
            json.put("username", credDto.getUserName());
            json.put("roles", user.getRole());
            json.put("userId", user.getUserId());

            return ResponseEntity.ok(json);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }


    public boolean forgotPassword(CredDTO credDto) {
        System.out.println("username"+credDto.getUserName());
        Optional<User> userOpt = userRepository.findByUserName(credDto.getUserName());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(credDto.getPassword()));
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            long totalVehicles = vehicleRepository.countAllVehicles();
            long availableVehicles = vehicleRepository.countAvailableVehicles();
            long totalUsers = userRepository.countCustomers();

            stats.put("totalVehicles", totalVehicles);
            stats.put("availableVehicles", availableVehicles);
            stats.put("totalUsers", totalUsers);

        } catch (Exception e) {
            throw new RuntimeException("Error while fetching dashboard stats", e);
        }
        return stats;
    }



}