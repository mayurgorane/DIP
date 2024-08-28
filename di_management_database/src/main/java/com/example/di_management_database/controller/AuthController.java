package com.example.di_management_database.controller;

import com.example.di_management_database.dto.AuthRequest;
import com.example.di_management_database.dto.PasswordResetRequest;
import com.example.di_management_database.entities.Company;
import com.example.di_management_database.entities.User;
import com.example.di_management_database.repository.UserRepository;
import com.example.di_management_database.filter.JwtService;
import com.example.di_management_database.service.AuthService;
import com.example.di_management_database.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class AuthController {


    @Autowired
    JwtService jwtService;

    @Autowired
    private AuthService authService;

    @CrossOrigin(origins = "http://192.168.21.39:4200")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        try {
            // Call the service to authenticate and generate token
            String token = authService.authenticateAndGenerateToken(authRequest.getEmail(), authRequest.getPassword());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            // Call the service to handle the logout process
            String message = authService.handleLogout(request);
            System.out.println("Successfully logout");
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            // Handle the exception and return an appropriate response

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/resetPassword")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<String> resetPassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody PasswordResetRequest passwordResetRequest) {

        // Extract the token from the header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return authService.resetPassword(token, passwordResetRequest);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided");
        }
    }

    @PostMapping("/forgot-password")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<String> forgotPassword(@RequestParam String email ) {
        return authService.forgotPassword(email);
    }

    @PostMapping("/reset-forgot-password")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<String> resetPassword(@RequestParam String token,  @RequestParam String newPassword) {
        return authService.resetForgetPassword(token, newPassword  );
    }

    @GetMapping("/validate")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        try {
            UUID tokenUUID = UUID.fromString(token);
            boolean isValid = authService.isTokenValid(tokenUUID);
            return ResponseEntity.ok(isValid);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

}
