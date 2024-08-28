package com.example.di_management_database.repository;

import com.example.di_management_database.entities.Company;
import com.example.di_management_database.entities.Workdb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface WorkdbRepository extends JpaRepository<Workdb,Long> {


    @Query("SELECT w FROM Workdb w WHERE w.company.com_guid = :comGuid")
    Optional<Workdb> findByCom_guid(@Param("comGuid") UUID comGuid);


}
