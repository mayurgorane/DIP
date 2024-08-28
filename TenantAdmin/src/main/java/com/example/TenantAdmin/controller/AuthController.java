package com.example.TenantAdmin.controller;


import com.example.TenantAdmin.dto.*;

import com.example.TenantAdmin.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login( @RequestBody AuthRequest authRequest) {
        return authService.authenticateAndGenerateToken( authRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return authService.logout(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided");
        }
    }

    @PostMapping("/resetPassword")
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
    public ResponseEntity<String> forgotPassword(@RequestParam String userName ) {
        return authService.forgotPassword( userName);
    }

    @PostMapping("/reset-forgot-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String comDname, @RequestParam String newPassword) {
        return authService.resetForgetPassword(token, newPassword,comDname);
    }



}