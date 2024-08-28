package com.example.TenantAdmin.repository;

import com.example.TenantAdmin.entities.Company;
import com.example.TenantAdmin.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    @Query("SELECT r FROM Role r WHERE r.roleName = :roleName")
    Optional<Role> findByRole_name(@Param("roleName") String roleName);
}
