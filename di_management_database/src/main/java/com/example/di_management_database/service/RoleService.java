package com.example.di_management_database.service;

import com.example.di_management_database.entities.IndustryType;
import com.example.di_management_database.entities.Role;
import com.example.di_management_database.repository.IndustryTypeRepository;
import com.example.di_management_database.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {


    @Autowired
    private RoleRepository roleRepository;

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
}
