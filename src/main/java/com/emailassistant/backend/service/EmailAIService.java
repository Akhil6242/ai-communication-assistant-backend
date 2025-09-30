package com.emailassistant.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailAIService {
    
    private static final String AI_SERVICE_URL = "http://localhost:5000";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public Map<String, Object> analyzeEmail(String subject, String body) {
        try {
            String url = AI_SERVICE_URL + "/api/analyze-email";
            
            // Create request payload
            Map<String, String> request = new HashMap<>();
            request.put("subject", subject);
            request.put("body", body);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Make request
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            return response.getBody();
            
        } catch (Exception e) {
            System.err.println("Error calling AI service: " + e.getMessage());
            // Return default values if AI service fails
            Map<String, Object> defaultResponse = new HashMap<>();
            defaultResponse.put("sentiment", "neutral");
            defaultResponse.put("priority", "Normal");
            defaultResponse.put("category", "General Support");
            defaultResponse.put("success", false);
            return defaultResponse;
        }
    }
    
    public String generateResponse(String emailBody, String sentiment, String category) {
        try {
            String url = AI_SERVICE_URL + "/api/generate-response";
            
            // Create request payload
            Map<String, String> request = new HashMap<>();
            request.put("body", emailBody);
            request.put("sentiment", sentiment);
            request.put("category", category);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Make request
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            return (String) responseBody.get("ai_response");
            
        } catch (Exception e) {
            System.err.println("Error generating AI response: " + e.getMessage());
            return "Thank you for your email. Our support team will get back to you shortly.";
        }
    }

    
}
