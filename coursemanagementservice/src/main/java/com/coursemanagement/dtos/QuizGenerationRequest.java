package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizGenerationRequest {
    private int moduleId;
    private int courseId;
    private String difficulty;
    private int noOfQuestions;
}
