package com.example.TenantAdmin.controller;

import com.example.TenantAdmin.dto.UserRequest;
import com.example.TenantAdmin.entities.User;


import com.example.TenantAdmin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class UserController {


    @Autowired
    private UserService userService;


    @PostMapping("/addUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> addUser(@RequestBody UserRequest userRequest, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");

        return userService.addUser(userRequest, token);
    }

    @GetMapping("/welcomeUser")
    public ResponseEntity<User> welcomeUser(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return userService.getUserDetailsFromToken(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @GetMapping("/verify-email/{tenantId}/{token}")
    public ResponseEntity<String> verifyEmail(@PathVariable UUID token,@PathVariable String tenantId) {
        return userService.verifyEmail(token,tenantId);
    }

}
