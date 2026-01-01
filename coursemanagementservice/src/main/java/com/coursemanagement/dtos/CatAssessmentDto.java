package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatAssessmentDto {
   private String title;
   private int durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<CatQuestionDto> questions;



}
