package com.example.di_management_database.service;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class TokenStorageService {

    // This could be replaced with a persistent store like Redis or a database
    private final Map<String, Set<String>> userTokens = new HashMap<>();

    public void storeToken(String email, String token) {
        userTokens.computeIfAbsent(email, k -> new HashSet<>()).add(token);
    }

    public Set<String> getTokensByEmail(String email) {
        return userTokens.getOrDefault(email, new HashSet<>());
    }

    public void removeTokensByEmail(String email) {
        userTokens.remove(email);
    }
}
