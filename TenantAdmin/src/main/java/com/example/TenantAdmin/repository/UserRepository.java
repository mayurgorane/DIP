package com.example.TenantAdmin.repository;

import com.example.TenantAdmin.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {




    @Query("SELECT c FROM User c WHERE c.verification_token = :verificationToken")
    Optional<User> findByVerification_token(@Param("verificationToken") UUID verificationToken);

    @Query("SELECT u FROM User u WHERE u.wkUserName = :userName")
    Optional<User> findByWkUserName(@Param("userName") String userName);
}

