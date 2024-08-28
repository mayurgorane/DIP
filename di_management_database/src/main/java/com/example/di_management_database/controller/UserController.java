package com.example.di_management_database.controller;

import com.example.di_management_database.dto.ApiResponse;
import com.example.di_management_database.dto.CompanyAdminRequest;
import com.example.di_management_database.dto.WorkdbDTO;
import com.example.di_management_database.entities.*;
import com.example.di_management_database.repository.CompanyRepository;
import com.example.di_management_database.repository.WorkdbRepository;
import com.example.di_management_database.repository.WorkspaceRepository;
import com.example.di_management_database.service.CompanyService;
import com.example.di_management_database.filter.JwtService;
import com.example.di_management_database.service.EmailService;
import com.example.di_management_database.service.UserService;
import com.example.di_management_database.service.WorkSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;


    @Autowired
    EmailService emailService;

    @Autowired
    WorkSpaceService workSpaceService;

    @Autowired
    JwtService jwtService;


    @PostMapping("/add-super-admin")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<?> addSuperAdmin(@RequestBody User userInfo) {
        return userService.addSuperAdmin(userInfo);
    }


    @PostMapping("/addCompanyAndAdmin")
    @PreAuthorize("hasRole('SUPER-ADMIN')")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<ApiResponse> addCompanyAndAdmin(@RequestBody CompanyAdminRequest request) {
        Company company = request.getCompany();
        Admin admin = request.getAdmin();
        Workdb workdb = request.getWorkdb();
        String industryType = request.getIndustryType();
        company.setCompanyName(company.getCompanyName().toLowerCase());
        try {
            companyService.addCompanyAndAdmin(company, workdb, admin, industryType);
            return ResponseEntity.ok(new ApiResponse(true, "Verification email sent to the user"));
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.contains("Company display name")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "comdname already present"));
            } else if (message.contains("Admin email already")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Admin email already present"));
            } else if (message.contains("Invalid industry type provided")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Invalid industry type"));
            }
            /* else if (message.contains("company_website_url already exists")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "company_website_url already present"));
            }else if (message.contains("Company name")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Company with the given name already present"));
            }
            else if (message.contains("Company email must be unique")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Company with the given email already present"));
            } else if (message.contains("Company with the given url")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Company with the given url already present"));
            }*/
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Invalid arguments provided: " + message));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Database access error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "An error occurred while adding Company and Workdb: " + e.getMessage()));
        }
    }

    @GetMapping("/workdb-by-com-dname")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<?> getWorkdbByComDname(@RequestParam String comDname, @RequestHeader("x-api-key") String apiKey) {
        try {
            WorkdbDTO workdbDTO = userService.getWorkdbByComDname(comDname, apiKey);
            if (workdbDTO == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key or Workdb not found.");
            }
            return ResponseEntity.ok(workdbDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the Workdb information.");
        }
    }

    @GetMapping("/verify-email/{token}")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<String> verifyEmail(@PathVariable UUID token) {
        try {
            String result = companyService.verifyEmailAndCreateAdmin(token);
            if (result.equals("Invalid verification token.") || result.equals("Workdb not found.")) {
                return ResponseEntity.badRequest().body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    @GetMapping("/getUser")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<?> getUserFromToken(@RequestHeader("Authorization") String token) {
        try {
            // Extracting the JWT token from the "Bearer " prefix
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Extract email from the token
            String email = jwtService.extractUsername(token);

            // Find the user by email
            Optional<User> userOptional = userService.findUserByEmail(email);

            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found for the provided email.");
            }

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the user information: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<Page<Company>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size) {
        try {
            Page<Company> companies = companyService.getAllCompanies(page, size);
            return ResponseEntity.ok(companies);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);


        }
    }
    @GetMapping("/search")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<Page<Company>> searchCompaniesByName(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Page<Company> companies = companyService.searchCompaniesByName(name, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(companies);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{companyId}/toggle-status")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<Company> toggleCompanyStatus(@PathVariable Long companyId) {
        Company updatedCompany = companyService.toggleCompanyStatus(companyId);
        return ResponseEntity.ok(updatedCompany);
    }
}