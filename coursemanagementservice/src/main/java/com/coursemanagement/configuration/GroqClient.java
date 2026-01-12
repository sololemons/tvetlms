package com.coursemanagement.configuration;

import com.coursemanagement.dtos.AiCatRequest;
import com.coursemanagement.dtos.AiQuizRequest;
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



    private final RestTemplate restTemplate;
    public GroqQuizResponse generateQuiz(AiQuizRequest aiQuizRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<AiQuizRequest> request =
                new HttpEntity<>(aiQuizRequest, headers);

        ResponseEntity<GroqQuizResponse> response =
                restTemplate.postForEntity(
                        groqUrl,
                        request,
                        GroqQuizResponse.class
                );

        if (response.getBody() == null) {
            throw new RuntimeException("AI service returned empty response");
        }

        return response.getBody();
    }
    public GroqQuizResponse generateCat(AiCatRequest aiCatRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<AiCatRequest> request =
                new HttpEntity<>(aiCatRequest, headers);

        ResponseEntity<GroqQuizResponse> response =
                restTemplate.postForEntity(
                        groqUrl,
                        request,
                        GroqQuizResponse.class
                );

        if (response.getBody() == null) {
            throw new RuntimeException("AI service returned empty response");
        }

        return response.getBody();
    }


}
