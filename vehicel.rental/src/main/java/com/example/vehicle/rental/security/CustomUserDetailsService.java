package com.example.vehicle.rental.security;

import com.example.vehicle.rental.repository.UserRepository;
import com.example.vehicle.rental.model.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.User; // For builder
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        com.example.vehicle.rental.model.User user = userRepository.findByEmail(input)
                .or(() -> userRepository.findByUserName(input)) // try username if email not found
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email/username: " + input));

        UserBuilder builder = User.withUsername(user.getEmail()) // always set Spring username as email
                .password(user.getPassword())
                .roles(user.getRole().name());

        return builder.build();
    }
}
