package com.example.vehicle.rental.repository;

import com.example.vehicle.rental.model.Role;
import com.example.vehicle.rental.model.User;
import com.example.vehicle.rental.model.UserStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
	@Query("SELECT u FROM User u WHERE u.email = :email")
	User getUserByEmail(String email);
	
	
    List<User> findByUserIdIn(List<Long> ids);

    Optional<User> findByUserName(String username);


    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER'")
    Long countCustomers();
    
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    
    
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.drivingLicenseNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.paymentMethod) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "UPPER(u.status) LIKE UPPER(CONCAT('%', :keyword, '%')) OR " +   // enum field
            "UPPER(u.role) LIKE UPPER(CONCAT('%', :keyword, '%'))")         // enum field
    List<User> searchUsers(@Param("keyword") String keyword);
}
