package com.example.di_management_database.service;

import com.example.di_management_database.dto.IndustryTypeDTO;
import com.example.di_management_database.entities.IndustryType;
import com.example.di_management_database.repository.IndustryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndustryTypeSerice {

    @Autowired
    private IndustryTypeRepository industryTypeRepository;

    public IndustryType createIndustryType(IndustryType industryType) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.systemDefault());

        industryType.setCreated_date(LocalDateTime.parse(dateTime.format(formatter), formatter));
        industryType.setModified_date(LocalDateTime.parse(dateTime.format(formatter), formatter));
        return industryTypeRepository.save(industryType);
    }


    ;

    public List<IndustryTypeDTO> getAllIndustryTypeNames() {
        return industryTypeRepository.findByIsActiveTrue()
                .stream()
                .map(industryType -> new IndustryTypeDTO(industryType.getIndustryType()))
                .collect(Collectors.toList());
    }
}
