package com.example.TenantAdmin.repository;

import com.example.TenantAdmin.entities.User;
import com.example.TenantAdmin.entities.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkSpaceRepository extends JpaRepository<WorkSpace,Long> {

    Optional<WorkSpace> findByUser(User user);
}
