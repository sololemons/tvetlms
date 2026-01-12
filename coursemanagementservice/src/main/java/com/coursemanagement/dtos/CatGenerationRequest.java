package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatGenerationRequest {
    private int catId;
    private int courseId;
    private String difficulty;
    private int noOfCloseEndedQuestions;
    private int noOfTrueFalseQuestions;
    private int noOfOpenEndedQuestions;
    private int noOfOptions;
}
