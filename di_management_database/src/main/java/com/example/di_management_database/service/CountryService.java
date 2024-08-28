package com.example.di_management_database.service;

import com.example.di_management_database.dto.Country;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CountryService {

    private final RestTemplate restTemplate;

    public CountryService() {
        this.restTemplate = new RestTemplate();
    }

    public List<String> getAllCountryNames() {
        String apiUrl = "https://restcountries.com/v3.1/all";
        JsonNode response = restTemplate.getForObject(apiUrl, JsonNode.class);

        List<String> countryNames = new ArrayList<>();
        if (response != null) {
            for (JsonNode node : response) {
                String countryName = node.path("name").path("common").asText();
                countryNames.add(countryName);
            }
        }
        return countryNames;
    }
}