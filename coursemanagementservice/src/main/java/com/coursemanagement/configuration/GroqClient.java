package com.coursemanagement.configuration;

import com.coursemanagement.dtos.GroqQuizResponse;
import com.coursemanagement.dtos.GroqResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GroqClient {

    @Value("${groq.api.url}")
    private String groqUrl;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;

    private final RestTemplate restTemplate;

    public GroqQuizResponse generateQuiz(String prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.2,
                "max_tokens", 1500,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<GroqResponseWrapper> response =
                restTemplate.postForEntity(
                        groqUrl,
                        request,
                        GroqResponseWrapper.class
                );

        if (response.getBody() == null || response.getBody().getChoices().isEmpty()) {
            throw new RuntimeException("Groq returned empty response");
        }

        return response.getBody().extractQuiz();
    }
}
