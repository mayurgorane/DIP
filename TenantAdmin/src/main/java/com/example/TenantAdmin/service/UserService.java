package com.example.TenantAdmin.service;

import com.example.TenantAdmin.dto.WorkdbDTO;
import com.example.TenantAdmin.entities.WorkSpace;
import com.example.TenantAdmin.repository.WorkSpaceRepository;
import com.example.TenantAdmin.tenantConfig.DynamicDataSource;
import com.example.TenantAdmin.tenantConfig.TenantContext;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.stereotype.Service;

import com.example.TenantAdmin.dto.UserRequest;
import com.example.TenantAdmin.entities.Company;
import com.example.TenantAdmin.entities.Role;
import com.example.TenantAdmin.entities.User;
import com.example.TenantAdmin.jwt.JwtUtil;
import com.example.TenantAdmin.repository.CompanyRepository;
import com.example.TenantAdmin.repository.RoleRepository;
import com.example.TenantAdmin.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.io.File;
import java.security.SecureRandom;
import java.util.*;

@Service
public class UserService {


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private EmailService emailService;

    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Autowired
    private WorkdbServiceClient workdbServiceClient;

    @Autowired
    WorkSpaceRepository workSpaceRepository;


    public ResponseEntity<String> addUser(UserRequest userRequest, String token) {
        // Extract tenant ID from the token
        String tenantId = jwtUtil.extractTenantId(token);
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // Find or create the company with ID = 1
        Optional<Company> company = companyRepository.findByCom_dname(tenantId);
        if (!company.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company with ID 1 does not exist");
        }

        // Extract the user from the request
        User user = userRequest.getUser();

        // Check if the email already exists
        Optional<User> existingUser = userRepository.findByWkUserName(user.getWkUserName());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this email already exists");
        }

        // Generate a random password and encode it
        String randomPassword = generateRandomPassword(8);
        UUID verificationToken = UUID.randomUUID(); // Generate a verification token

        // Set properties for the user
        user.setWkUserGuid(UUID.randomUUID());
        user.setCompany(company.get());
        user.setWkUserName(user.getFirstName() + "." + user.getLastName() + "@" + user.getCompany().getCom_dname());
        user.setCreatedDate(new Date());
        user.setVerification_token(verificationToken); // Store the token in the user entity
        user.setIs_email_verified(false); // Set email verification status to false
        user.setCreatedBy("admin");
        // Assign role "User" to the new user
        Role userRole = roleRepository.findByRole_name("USER").orElseThrow(() -> new RuntimeException("Role 'User' not found"));
        user.setRole(userRole);
        // Save the user temporarily to generate the verification token
        userRepository.save(user);

        createWorkspaceForUser(user,  token);

        // Send verification email
        String verificationUrl = String.format("http://192.168.21.39:9090/api/auth/verify-email/%s/%s", tenantId, verificationToken);
        String emailSubject = "Verify Your Email Address";
        String emailText = String.format("Dear %s %s,\n\nPlease verify your email address by clicking the following link:\n\n%s\n\nBest regards,\nYour Company", user.getFirstName(), user.getLastName(), verificationUrl);
        emailService.sendEmail(user.getEmail(), emailSubject, emailText);

        return ResponseEntity.status(HttpStatus.OK).body("Verification email sent. Please verify your email address.");
    }


    public ResponseEntity<String> verifyEmail(UUID token, String tenantId) {

        DataSource tenantDataSource = dynamicDataSource.getTenantDataSource(tenantId);

        if (tenantDataSource == null) {
            // Retrieve tenant-specific details and configure DataSource

            WorkdbDTO workdbDTO = workdbServiceClient.getWorkdbByComDname(tenantId);
            String workdbName = workdbDTO.getWorkdb_name().replace('-', '_');
            String url = workdbDTO.getServer_name() + workdbName;
            String username = workdbDTO.getDbuser_name();
            String password = workdbDTO.getPassword();

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(url);

            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
            hikariConfig.setDriverClassName("org.postgresql.Driver");
            System.out.println("New connection");
            dynamicDataSource.setTenantDataSource(tenantId, hikariConfig);

        }

        if (tenantDataSource != null) {
            System.out.println("old connection");
        }

        TenantContext.setCurrentTenant(tenantId);

        Optional<User> optionalUser = userRepository.findByVerification_token(token);
        System.out.println("optionalUser" + optionalUser);

        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification token");
        }

        User user = optionalUser.get();
        if (user.getIs_email_verified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already verified");
        }

        UUID companyGuid = user.getCompany().getComGuid();
        Optional<Company> optionalCompany = companyRepository.findByCom_guid(companyGuid);
        Company company = optionalCompany.get();
        // Generate a new random password for the account information email
        String randomPassword = generateRandomPassword(8);


        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setVerification_token(null); // Clear the verification token
        user.setIs_email_verified(true); // Mark the user as verified
        userRepository.save(user); // Save the user with updated status

        // Send account information email after verification
        // Extract tenantId or use a static tenantId if applicable
        String emailSubject = "Your Account Information";

        String emailText = String.format(
                "Dear %s %s,\n\nYour account has been verified and created successfully.\n\n" +
                        "Please login with your username and password given below.\n\n" +
                        "Login URL: http://localhost:9090/api/auth/login/%s\n" +
                        "Username: %s\n" +
                        "Password: %s\n\n" +
                        "Best regards,\nYour Company",
                user.getFirstName(),
                user.getLastName(),
                tenantId,
                user.getWkUserName(),  // Assuming user.getUsername() is the method to get the username
                randomPassword
        );
        emailService.sendEmail(user.getEmail(), emailSubject, emailText);

        return ResponseEntity.status(HttpStatus.OK).body("Email verified successfully. Account information has been sent.");
    }


    private String generateRandomPassword(int length) {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    public ResponseEntity<User> getUserDetailsFromToken(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            Optional<User> userOptional = userRepository.findByWkUserName(email);

            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            // Log the exception and return unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    private void createWorkspaceForUser(User user, String token) {
        String adminUsername = jwtUtil.extractUsername(token);
        Optional<User> adminOptional = userRepository.findByWkUserName(adminUsername);

        if (adminOptional.isEmpty()) {
            throw new RuntimeException("Admin user not found");
        }

        User admin = adminOptional.get();
        Optional<WorkSpace> workSpaceOptional = workSpaceRepository.findByUser(admin);

        if (workSpaceOptional.isEmpty()) {
            throw new RuntimeException("Workspace for admin not found");
        }

        WorkSpace workSpace = workSpaceOptional.get();
        String companyWorkspacePath = workSpace.getComWorkspacePath();
        String userWorkspacePath = companyWorkspacePath + "\\" + user.getFirstName();

        File userWorkspaceDir = new File(userWorkspacePath);
        if (!userWorkspaceDir.exists() && !userWorkspaceDir.mkdirs()) {
            throw new RuntimeException("Failed to create workspace directory for user");
        }

        WorkSpace workspaceUser = new WorkSpace();
        workspaceUser.setComGuid(workSpace.getComGuid());
        workspaceUser.setComWorkspaceGuid(workSpace.getComWorkspaceGuid());
        workspaceUser.setUser(user);
        workspaceUser.setWorkspaceGuid(UUID.randomUUID());
        workspaceUser.setWorkspaceName(user.getFirstName());
        workspaceUser.setComWorkspacePath(workSpace.getComWorkspacePath());
        workspaceUser.setWorkspacePath(userWorkspacePath);
        workspaceUser.setStorageLimit((short) 10);
        workspaceUser.setIsActive(true);
        workspaceUser.setIsRemoved(false);
        workspaceUser.setCreatedBy(1);

        workSpaceRepository.save(workspaceUser);
    }
}