package com.example.TenantAdmin.service;

import com.example.TenantAdmin.dto.AuthRequest;
import com.example.TenantAdmin.dto.JwtResponse;
import com.example.TenantAdmin.dto.PasswordResetRequest;
import com.example.TenantAdmin.dto.WorkdbDTO;
import com.example.TenantAdmin.entities.User;
import com.example.TenantAdmin.jwt.JwtUtil;
import com.example.TenantAdmin.repository.UserRepository;
import com.example.TenantAdmin.tenantConfig.DynamicDataSource;
import com.example.TenantAdmin.tenantConfig.TenantContext;
import com.zaxxer.hikari.HikariConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private WorkdbServiceClient workdbServiceClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    EmailService emailService;

    @PostConstruct
    public void init() {
        // Setting initial data source properties to entityManagerFactory
        entityManagerFactory.setDataSource(dynamicDataSource);
        entityManagerFactory.afterPropertiesSet();
    }

    public ResponseEntity<?> authenticateAndGenerateToken(AuthRequest authRequest) {
        try {
            String comDname =  extractCompanyName(authRequest.getUsername());
            System.out.println("comDname"+comDname);
             setupTenantDataSource(comDname);
            // Authenticate user and generate JWT token
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwtToken = jwtUtil.generateToken(userDetails, comDname);
            System.out.println("jwtToken"+jwtToken);
            return ResponseEntity.ok(new JwtResponse(jwtToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    public static String extractCompanyName(String username) {
        // Assuming the username is in the format 'name@company'
        String[] parts = username.split("@");
        if (parts.length > 1) {
            return parts[1]; // Return the part after '@' which is the company name
        }
        return ""; // Handle cases where the format is incorrect
    }


    public ResponseEntity<?> logout(String token) {
        try {
            // Blacklist the token
            jwtUtil.blacklistToken(token);

            // You may also add logic to remove any associated session data if necessary

            // Return successful response
            return ResponseEntity.ok("Successfully logged out");
        } catch (Exception e) {
            // Log the exception and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during logout");
        }
    }


    public ResponseEntity<String> resetPassword(String token, PasswordResetRequest passwordResetRequest) {
        // Extract the email from the JWT token
        String email = jwtUtil.extractUsername(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // Find the user by email
        User user = userRepository.findByWkUserName(email).orElse(null);
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

    public ResponseEntity<String> forgotPassword(String userName) {
        String comDname = extractCompanyName(userName);
        setupTenantDataSource(comDname);

        Optional<User> userOptional = userRepository.findByWkUserName(userName);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        // Create a new password reset token with a 1-hour expiration
        UUID resetTokenWithExpiration = TokenUtils.generateTokenWithExpiration();
        user.setVerification_token(resetTokenWithExpiration);
        userRepository.save(user);

        // Send reset email
        String resetUrl = String.format("http://192.168.21.39:9090/api/auth/reset-forgot-password?comDname=%s&token=%s", comDname, resetTokenWithExpiration);
        String emailSubject = "Password Reset Request";
        String emailText = String.format("Dear %s,\n\nTo reset your password, please click the following link:\n\n%s\n\nThe link will expire in 1 hour.\n\nBest regards,\nYour Company", user.getFirstName(), resetUrl);
        emailService.sendEmail(user.getEmail(), emailSubject, emailText);

        return ResponseEntity.ok("Password reset email sent");
    }

    public ResponseEntity<String> resetForgetPassword(String tokenWithExpiration, String newPassword, String comDname) {

        setupTenantDataSource(comDname);

        Optional<User> userOptional = userRepository.findByVerification_token(UUID.fromString(tokenWithExpiration));
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");

        }

        User user = userOptional.get();

        if (TokenUtils.isTokenExpired(tokenWithExpiration)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerification_token(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful");
    }


    private void setupTenantDataSource(String comDname) {
        try {
            DataSource tenantDataSource = dynamicDataSource.getTenantDataSource(comDname);

            if (tenantDataSource == null) {
                // Retrieve tenant-specific details and configure DataSource
                WorkdbDTO workdbDTO = workdbServiceClient.getWorkdbByComDname(comDname);

                // Handle potential null values or missing fields in workdbDTO
                if (workdbDTO == null) {
                    throw new RuntimeException("No tenant configuration found for: " + comDname);
                }

                String workdbName = workdbDTO.getWorkdb_name();
                if (workdbName == null || workdbName.isEmpty()) {
                    throw new RuntimeException("Invalid workdb_name for: " + comDname);
                }

                workdbName = workdbName.replace('-', '_');
                String url = workdbDTO.getServer_name() + workdbName;
                String username = workdbDTO.getDbuser_name();
                String password = workdbDTO.getPassword();

                if (url == null || username == null || password == null) {
                    throw new RuntimeException("Missing database connection details for: " + comDname);
                }

                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setJdbcUrl(url);
                hikariConfig.setUsername(username);
                hikariConfig.setPassword(password);
                hikariConfig.setDriverClassName("org.postgresql.Driver");

                // Set HikariCP configuration
                hikariConfig.setIdleTimeout(1 * 60 * 1000); // 30 minutes
                hikariConfig.setMaxLifetime(2 * 60 * 1000); // 1 hour
                hikariConfig.setConnectionTimeout(30 * 1000); // 30 seconds

                System.out.println("New connection");
                dynamicDataSource.setTenantDataSource(comDname, hikariConfig);
            } else {
                System.out.println("Old connection");
            }

            TenantContext.setCurrentTenant(comDname);

        } catch (RuntimeException e) {
            // Handle known runtime exceptions
            System.err.println("Error setting up tenant data source: " + e.getMessage());
            throw e; // Re-throw the exception to be handled by higher-level exception handling, if necessary
        } catch (Exception e) {

            System.err.println("Unexpected error while setting up tenant data source: " + e.getMessage());
            throw new RuntimeException("Failed to set up tenant data source", e);
        }
    }


}
