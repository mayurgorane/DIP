package com.example.di_management_database.service;

import com.example.di_management_database.dto.CustomUserDetails;
import com.example.di_management_database.dto.WorkdbDTO;
import com.example.di_management_database.entities.Company;
import com.example.di_management_database.entities.User;
import com.example.di_management_database.entities.Workdb;
import com.example.di_management_database.filter.JwtService;
import com.example.di_management_database.repository.CompanyRepository;
import com.example.di_management_database.repository.UserRepository;
import com.example.di_management_database.repository.WorkdbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    WorkdbRepository workdbRepository;

    @Autowired
    private JwtService jwtService;

    private static final String API_KEY = "asdgdfajdvjn;dfkjgb6546g54sdgd3654f65465dsfg54s5";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user by email
        Optional<User> userInfo = userRepository.findByEmail(email);

        // Convert User entity to UserInfoDetails if present, else throw exception
        UserDetails userDetails = userInfo.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return userDetails;
    }


    public ResponseEntity<?> addSuperAdmin(@RequestBody User userInfo) {
        Optional<User> existingUser = userRepository.findByUserName(userInfo.getUserName());
        Optional<User> existingUserEmail = userRepository.findByEmail(userInfo.getEmail());
        String combinedUsername = userInfo.getFirst_name() + "." + userInfo.getLast_name();
        if (existingUser.isPresent() && existingUserEmail.isPresent()) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "Username and Email already exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
        } else if (existingUser.isPresent()) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
        } else if (existingUserEmail.isPresent()) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
        }

        String encodedPassword = passwordEncoder.encode(userInfo.getPassword());
        userInfo.setPassword(encodedPassword);
          userInfo.setUserName(combinedUsername);
        userRepository.save(userInfo);

        // Create UserDetails from the saved User entity
        UserDetails userDetails = new CustomUserDetails(userInfo);
        String token = jwtService.generateToken(userDetails); // Use UserDetails

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userInfo);

        return ResponseEntity.ok(response);
    }

    public WorkdbDTO getWorkdbByComDname(String comDname, String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return null; // Invalid API Key
        }

        // Find the company by com_dname
        Optional<Company> companyOptional = companyRepository.findByComDname(comDname);
        if (!companyOptional.isPresent()) {
            return null; // Company not found
        }

        Company company = companyOptional.get();
        UUID comGuid = company.getCom_guid();

        // Find the workdb by com_guid
        Optional<Workdb> workdbOptional = workdbRepository.findByCom_guid(comGuid);
        if (!workdbOptional.isPresent()) {
            return null; // Workdb not found
        }

        Workdb workdb = workdbOptional.get();

        // Convert Workdb to WorkdbDTO
        return convertToDTO(workdb);
    }

    private boolean isValidApiKey(String apiKey) {
        return API_KEY.equals(apiKey);
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    LocalDateTime dateTime = LocalDateTime.now(ZoneId.systemDefault());

    private WorkdbDTO convertToDTO(Workdb workdb) {
        WorkdbDTO dto = new WorkdbDTO();
        dto.setWorkdb_id(workdb.getWorkdb_id());
        dto.setWorkdb_guid(workdb.getWorkdb_guid());
        dto.setWorkspace_guid(workdb.getWorkspace_guid());
        dto.setServer_name(workdb.getServer_name());
        dto.setWorkdb_name(workdb.getWorkdb_name());
        dto.setDbuser_name(workdb.getDbuser_name());
        dto.setPort(workdb.getPort());
        dto.setIs_active(workdb.getIs_active());
        dto.setIs_removed(workdb.getIs_removed());
        dto.setCreated_by(workdb.getCreated_by());
        dto.setCreated_date(workdb.getCreated_date());
        dto.setModified_by(workdb.getModified_by());
        dto.setModified_date(workdb.getModified_date());
        dto.setPassword(workdb.getPassword());
        return dto;
    }



    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}