package com.coursemanagement.dtos;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;

@Data
public class GroqResponseWrapper {

    private List<Choice> choices;

    public GroqQuizResponse extractQuiz() {
        try {
            String content = choices.getFirst().getMessage().getContent();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, GroqQuizResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse quiz from Groq response", e);
        }
    }

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}

