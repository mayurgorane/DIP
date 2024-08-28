package com.example.di_management_database.service;

import com.example.di_management_database.dto.PasswordResetRequest;
import com.example.di_management_database.entities.Company;
import com.example.di_management_database.entities.User;
import com.example.di_management_database.filter.JwtService;
import com.example.di_management_database.repository.CompanyRepository;
import com.example.di_management_database.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    TokenStorageService tokenStorageService;

    public String authenticateAndGenerateToken(String email, String password) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            if (authentication.isAuthenticated()) {
                // Fetch user details
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                // Generate token
                String token = jwtService.generateToken(userDetails);

                // Store the token
                tokenStorageService.storeToken(email, token);

                return token;
            } else {
                throw new RuntimeException("Email and password does not match, please try again");
            }
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("User not found");
        } catch (Exception e) {
            throw new RuntimeException("Email and password does not match, please try again");
        }
    }

    public String handleLogout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                // Blacklist the token
                jwtService.blacklistToken(token);

                // Additional logic to remove any associated session data if necessary
                return "Successfully logged out";
            } catch (Exception e) {
                // Handle the exception and return an appropriate message
                throw new RuntimeException("An error occurred during logout");
            }
        } else {
            throw new RuntimeException("No token provided");
        }
    }

    public ResponseEntity<String> resetPassword(String token, PasswordResetRequest passwordResetRequest) {
        // Extract the email from the JWT token
        String email = jwtService.extractUsername(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // Find the user by email
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Check if the old password matches
        if (!passwordEncoder.matches(passwordResetRequest.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful");
    }

    public ResponseEntity<String> forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with the given email");
        }

        User user = userOptional.get();

        // Create a new password reset token with a 1-hour expiration
        UUID resetTokenWithExpiration = TokenUtils.generateTokenWithExpiration();
        user.setVerification_token(resetTokenWithExpiration);
        userRepository.save(user);

        // Send reset email
        String resetUrl = String.format("http://192.168.21.39:4200/login/reset-password?token=%s", resetTokenWithExpiration);
        String emailSubject = "Password Reset Request";
        String emailText = String.format(
                "It looks like you requested a password reset for your account. To reset your password, click the link below:\n\n" +
                        "Reset Password link: %s\n\n" +
                        "If you didn’t request this change, please ignore this email. Your account remains secure.\n\n" +
                        "Thank you,\n" +
                        "Data Intelligence Platform Support Team",
                resetUrl
        );
        emailService.sendEmail(user.getEmail(), emailSubject, emailText);

        return ResponseEntity.ok("Password reset email sent");
    }

    public ResponseEntity<String> resetForgetPassword(String tokenWithExpiration, String newPassword) {


        Optional<User> userOptional = userRepository.findByVerification_token(UUID.fromString(tokenWithExpiration));
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");

        }

        User user = userOptional.get();


        if (TokenUtils.isTokenExpired(tokenWithExpiration)) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired");

        }

        Set<String> tokens = tokenStorageService.getTokensByEmail(userOptional.get().getEmail());
        for (String token : tokens) {
            jwtService.blacklistToken(token);
        }
        tokenStorageService.removeTokensByEmail(userOptional.get().getEmail());

      // Update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerification_token(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful");
    }


    public boolean isTokenValid(UUID token) {
        Optional<User> userOptional = userRepository.findByVerification_token(token);
        return userOptional.isPresent();
    }

}
