package com.coursemanagement.dtos;

import com.shared.dtos.ModuleDto;
import com.coursemanagement.entity.CourseOverview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private String courseName;
    private String description;
    private CourseOverview courseOverview;
    private List<ModuleDto> moduleDto;
}
