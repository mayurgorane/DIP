package com.example.di_management_database.repository;


import com.example.di_management_database.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);

    @Query("SELECT c FROM User c WHERE c.verification_token = :verificationToken")
    Optional<User> findByVerification_token(@Param("verificationToken") UUID verificationToken);

    Optional<User> findByEmail(String email);
}
