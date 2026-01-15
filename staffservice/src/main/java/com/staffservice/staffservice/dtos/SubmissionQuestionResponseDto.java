package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionQuestionResponseDto {

    private String question;
    private String studentAnswer;

    private Boolean correct;
    private String correctAnswer;
    private Double marksAwarded;
    private Double maxMarks;
}
