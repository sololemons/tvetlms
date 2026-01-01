package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroqQuizResponse {

    private String quizId;
    private String topic;
    private String difficultyLevel;

    private List<McqQuestion> mcqQuestions;
    private List<TrueFalseQuestion> trueFalseQuestions;
    private List<OpenEndedQuestion> openEndedQuestions;

    private int totalQuestions;
    private int totalPoints;
    private int estimatedDurationMinutes;
}

