package com.coursemanagement.dtos;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GroqQuizResponse {
    private String quiz_id;
    private String generated_at;
    private String difficulty_level;
    private int total_questions;

    private List<MCQ> multiple_choice;
    private List<TrueFalse> true_false;
    private List<ShortAnswer> short_answer;

    @Data
    public static class MCQ {
        private String question;
        private Map<String, String> options;
        private String correct_answer;
        private String explanation;
    }

    @Data
    public static class TrueFalse {
        private String question;
        private boolean correct_answer;
        private String explanation;
    }

    @Data
    public static class ShortAnswer {
        private String question;
        private List<String> key_points;
        private String sample_answer;
    }
}
