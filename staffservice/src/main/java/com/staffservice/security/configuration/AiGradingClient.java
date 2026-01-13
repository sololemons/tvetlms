package com.staffservice.security.configuration;

import com.shared.dtos.AiCatGradeRequest;
import com.shared.dtos.AiGradeRequest;
import com.shared.dtos.AiGradeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiGradingClient {

    private final RestTemplate restTemplate;

    @Value("${ai.grading.url}")
    private String aiGradingUrl;

    @Value("${ai.grading.api-key}")
    private String apiKey;

    public AiGradeResponse gradeQuiz(AiGradeRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);

            HttpEntity<AiGradeRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AiGradeResponse> response = restTemplate.exchange(
                    aiGradingUrl,
                    HttpMethod.POST,
                    entity,
                    AiGradeResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("AI grading failed, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to grade submission: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error calling AI grading service: {}", e.getMessage(), e);
            throw new RuntimeException("Error calling AI grading service", e);
        }
    }
    public AiGradeResponse gradeCat(AiCatGradeRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);

            HttpEntity<AiCatGradeRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AiGradeResponse> response = restTemplate.exchange(
                    aiGradingUrl,
                    HttpMethod.POST,
                    entity,
                    AiGradeResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("AI grading failed, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to grade submission: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error calling AI grading service: {}", e.getMessage(), e);
            throw new RuntimeException("Error calling AI grading service", e);
        }
    }
}
