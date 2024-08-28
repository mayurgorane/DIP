package com.example.TenantAdmin.service;

import com.example.TenantAdmin.entities.User;

import com.example.TenantAdmin.repository.UserRepository;
import com.example.TenantAdmin.tenantConfig.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        String tenantId = TenantContext.getCurrentTenant();


        if (tenantId == null) {
            throw new UsernameNotFoundException("No tenant context found");
        }
        // Ensure that the tenant-specific data source is being used
        System.out.println("Loading user with user name: " + userName + " for tenant: " + tenantId);

        // The userRepository should be using the dynamic data source
        User user = userRepository.findByWkUserName(userName).orElseThrow(() -> new UsernameNotFoundException("User not found with user name: " + userName));

        Collection<? extends GrantedAuthority> authorities = user.getRole() != null ? List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName().toUpperCase())) : List.of();

        return new org.springframework.security.core.userdetails.User(user.getWkUserName(), user.getPassword(), authorities);
    }


}