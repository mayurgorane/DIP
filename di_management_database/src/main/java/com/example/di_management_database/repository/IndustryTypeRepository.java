package com.example.di_management_database.repository;

import com.example.di_management_database.entities.IndustryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndustryTypeRepository extends JpaRepository<IndustryType,Long> {
    @Query("SELECT i FROM IndustryType i WHERE i.industryType = :industry_type")
    Optional<IndustryType> findByIndustryType(@Param("industry_type") String industry_type);


    @Query("SELECT it FROM IndustryType it WHERE it.is_active = true")
    List<IndustryType> findByIsActiveTrue();
}


