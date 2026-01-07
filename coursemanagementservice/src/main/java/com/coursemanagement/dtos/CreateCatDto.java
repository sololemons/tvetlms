package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCatDto {
    private String catTitle;
    private String catDescription;
    private int durationMinutes;
    private int courseId;
    private String startTime;
}
