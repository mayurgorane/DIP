package com.example.di_management_database.controller;

import com.example.di_management_database.entities.IndustryType;
import com.example.di_management_database.entities.Role;
import com.example.di_management_database.service.IndustryTypeSerice;
import com.example.di_management_database.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/api/roles")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role newRole = roleService.createRole(role);
        return ResponseEntity.ok(role);
    }
}