package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionViewDto {

    private Long submissionId;
    private String studentAdmissionId;
    private long targetId;
    private String quizTitle;
    private Double totalAwardedPoints;
    private Double totalMaxPoints;


    private List<SubmissionQuizQuestionViewDto> questions;
}
