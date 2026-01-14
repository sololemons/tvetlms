package com.gradeservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionGradeDto {

    private String studentAdmissionId;
    private String courseId;
    private String targetId;
    private double totalPoints;
    private double maxPoints;
    private double percentage;
    private LocalDateTime gradedAt;


}
