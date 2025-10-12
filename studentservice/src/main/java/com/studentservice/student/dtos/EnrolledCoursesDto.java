package com.studentservice.student.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnrolledCoursesDto {
    private String courseNames;
    private String progression;
    private String isCompleted;
    private long courseId;
    private List<EnrolledModuleDto> enrollModuleDto;
}
