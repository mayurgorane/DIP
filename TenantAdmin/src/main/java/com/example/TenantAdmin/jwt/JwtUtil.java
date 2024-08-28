package com.example.TenantAdmin.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final Key secretKey;

    private Set<String> blacklistedTokens = new HashSet<>();

    // Constructor injection to initialize the key
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // Ensure the secret key is secure enough for HS256
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails, String tenantId) {
        return createToken(userDetails.getUsername(), tenantId);
    }
    private String createToken(String username, String tenantId) {
        return Jwts.builder()
                .setSubject(username) // Use username as the subject
                .claim("tenantId", tenantId) // Add tenant ID claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        // Extract the username from the token
        final String username = extractUsername(token);

        // Check if the token is blacklisted
        if (isTokenBlacklisted(token)) {
            return false;
        }

        // Validate that the token matches the user details and is not expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractTenantId(String token) {
        String tenantId = extractClaim(token, claims -> claims.get("tenantId", String.class));
        System.out.println("Extracted Tenant ID from token: " + tenantId); // Debugging log
        return tenantId;
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

}