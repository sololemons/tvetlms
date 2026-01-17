package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentGradeRequestDto {
    private long awardedMarks;
    private String feedback;
    private int submissionId;
}
