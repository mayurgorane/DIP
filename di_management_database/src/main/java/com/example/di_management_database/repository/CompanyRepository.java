package com.example.di_management_database.repository;
import com.example.di_management_database.entities.Company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("SELECT c FROM Company c WHERE c.comDname = :comDname")
    Optional<Company> findByComDname(@Param("comDname") String comDname);


    @Query("SELECT COUNT(c) > 0 FROM Company c WHERE c.comDname = :comDname")
    boolean existsByComDname(@Param("comDname") String comDname);

    @Query("SELECT COUNT(c) > 0 FROM Company c WHERE c.companyName = :companyName")
    boolean existsByCompanyName(@Param("companyName") String companyName);

    @Query("SELECT COUNT(c) > 0 FROM Company c WHERE c.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(c) > 0 FROM Company c WHERE c.company_website_url = :company_website_url")
    boolean existsByCompany_website_url(@Param("company_website_url") String company_website_url);

    @Query("SELECT c FROM Company c WHERE c.verification_token = :verificationToken")
    Company findByVerification_token(@Param("verificationToken") UUID verificationToken);

    @Query("SELECT c FROM Company c WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Company> searchByCompanyName(@Param("name") String name, Pageable pageable);


    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Company a WHERE a.adm_email = :email")
    boolean existsByAdmEmail(@Param("email") String email);

}