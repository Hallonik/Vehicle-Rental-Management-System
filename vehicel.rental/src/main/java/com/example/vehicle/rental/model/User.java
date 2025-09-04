package com.example.vehicle.rental.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.vehicle.rental.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;


import lombok.*;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails  {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; 


    
    
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


   


    @Column(name="userName",unique = true,nullable = false)
    @NotBlank(message = "Username is required")   
    private String userName;
    
    @Override
    public String getUsername () {
    	return this.userName;
    }
   
    

    public void setUsername(String userName) {
        this.userName = userName;
    }

   

   

    private String getStripe_customer_id() {
        return Stripe_customer_id;
    }

    public void setStripe_customer_id(String stripe_customer_id) {
        this.Stripe_customer_id = stripe_customer_id;
    }

    private String Stripe_customer_id;
    
    
    
    
    
    

    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone number must be 10 digits"
    )
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Password is required")   
    @Size(min=8,message="Password should be 8 character long")
    @Column(nullable = false)
    private String password;

    // Optional fields (no @NotBlank so they can be null)
    @Size(max = 50, message = "Driving license number must be at most 50 characters")
    private String drivingLicenseNumber;

    @Size(max = 50, message = "Payment method must be at most 50 characters")
    private String paymentMethod;

  

    @Column(nullable = false)
    private boolean isEmailVerified;
    
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    @Builder.Default
    private UserStatus status= UserStatus.ACTIVE;;


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		 return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
	}

	


	
	
	

}
