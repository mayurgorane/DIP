package com.example.di_management_database.repository;

import com.example.di_management_database.entities.Workdb;
import com.example.di_management_database.entities.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace,Long> {



}
