package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionQuizQuestionViewDto {

    private Integer questionId;
    private String questionText;
    private Set<String> options;

    private String studentAnswer;
    private String correctAnswer;

    private Double awardedMarks;
    private Double maxMarks;
    private String feedback;


}
