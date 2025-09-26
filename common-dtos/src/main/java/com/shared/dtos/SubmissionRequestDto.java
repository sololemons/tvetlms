package com.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionRequestDto {
    private String admissionId;
    private String className;
    private long assignmentId;
    private List<AnswerDto> answerText;
}
