package com.example.TenantAdmin.repository;

import com.example.TenantAdmin.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {
    @Query("SELECT c FROM Company c WHERE c.comGuid = :comGuid")
    Optional<Company> findByCom_guid(@Param("comGuid") UUID comGuid);

    @Query("SELECT c FROM Company c WHERE c.com_dname = :com_dname")
    Optional<Company> findByCom_dname(@Param("com_dname")  String com_dname);
}
