package com.example.TenantAdmin.service;

import com.example.TenantAdmin.dto.WorkdbDTO;
import com.example.TenantAdmin.tenantConfig.DynamicDataSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkdbServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8080/api/users";


    public WorkdbDTO getWorkdbByComDname(String comDname) {
        String url = BASE_URL + "/workdb-by-com-dname?comDname=" + comDname;
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "asdgdfajdvjn;dfkjgb6546g54sdgd3654f65465dsfg54s5");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<WorkdbDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, WorkdbDTO.class);
        return response.getBody();
    }



}