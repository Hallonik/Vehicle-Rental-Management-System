/*package com.example.vehicle.rental.controller;


import com.example.vehicle.rental.dto.CredDTO;
import com.example.vehicle.rental.dto.UserDto;
import com.example.vehicle.rental.service.UserService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "AdminController", description = "Endpoints for Admin level operations")
public class AdminController {

    public UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    

    @PostMapping(
    value = "/SignUp",
    consumes =  "application/json",
    produces =  "application/json"
    )
    public ResponseEntity<?> addUser(@RequestBody UserDto user) throws StripeException {

        return userService.addUser(user);

    }
    
    @PostMapping("/SignIn")
    public ResponseEntity<?> signIn(@RequestBody CredDTO credDto) {
        // authenticate() should return a JWT token wrapped in the response
        return userService.authenticate(credDto);
    }
}
*/

package com.example.vehicle.rental.controller;

import com.example.vehicle.rental.dto.CredDTO;
import com.example.vehicle.rental.dto.UserDto;
import com.example.vehicle.rental.repository.UserRepository;
import com.example.vehicle.rental.security.JwtService;
import com.example.vehicle.rental.service.UserService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Controller", description = "Endpoints for Admin level operations")
public class    AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AdminController(UserService userService, UserRepository userRepository,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/Authenticate")
    public ResponseEntity<?> authenticate(@RequestBody CredDTO credDto) {
        return userService.authenticate(credDto);
    }

    // SIGN IN
    @PostMapping("/SignIn")
    public ResponseEntity<?> signIn(@RequestBody CredDTO credDto) {
        // Authenticate user
        return userService.authenticate(credDto);
    }

    // SIGN UP + Auto Token Generation
    @PostMapping(value = "/Signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> signUp(@RequestBody UserDto userDto) throws StripeException {
        userService.addUser(userDto); // Should hash password internally


        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

           var user = userRepository.findByUserName(userDetails.getUsername());

           if(user.isEmpty()){
               return ResponseEntity.status(401).body("access denied");
           }

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());
            response.put("userId", user.get().getUserId());
            response.put("roles", user.get().getRole());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    @PostMapping(value = "/forgotPassword", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> forgotPassword(@RequestBody CredDTO credDto) {
        boolean updated = userService.forgotPassword(credDto);

        if (updated) {
            return ResponseEntity.ok("{\"message\": \"Password updated successfully\"}");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Username not found\"}");
        }
    }

    @GetMapping("/Get_admin_dash_summary")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = userService.getDashboardStats();

            if (stats == null || stats.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "No dashboard data available.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            stats.put("status", "success");
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch dashboard summary.");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



}

