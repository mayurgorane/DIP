package com.example.di_management_database.controller;

import com.example.di_management_database.dto.IndustryTypeDTO;
import com.example.di_management_database.entities.IndustryType;
import com.example.di_management_database.service.IndustryTypeSerice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/industry-types")
public class IndustryTypeController {

    @Autowired
    private IndustryTypeSerice industryTypeService;

    @PostMapping
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<IndustryType> createIndustryType(@RequestBody IndustryType industryType) {
        IndustryType newIndustryType = industryTypeService.createIndustryType(industryType);
        return ResponseEntity.ok(newIndustryType);
    }

    @GetMapping("/names")
    @CrossOrigin(origins = "http://192.168.21.39:4200")
    public ResponseEntity<List<IndustryTypeDTO>> getAllIndustryTypeNames() {
        List<IndustryTypeDTO> industryTypeNames = industryTypeService.getAllIndustryTypeNames();
        return ResponseEntity.ok(industryTypeNames);
    }

}