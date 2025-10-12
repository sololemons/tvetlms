package com.studentservice.student.utilis;

import com.studentservice.student.dtos.EnrolledCoursesDto;
import com.studentservice.student.dtos.EnrolledModuleDto;
import com.studentservice.student.entities.EnrolledCourses;
import com.studentservice.student.entities.EnrolledModules;
import java.util.List;
import java.util.stream.Collectors;

public class EnrolledCourseMapper {

    public static EnrolledCoursesDto toDto(EnrolledCourses course) {
        EnrolledCoursesDto dto = new EnrolledCoursesDto();
        dto.setCourseNames(course.getCourseName());
        dto.setProgression(course.getProgression());
        dto.setCourseId(course.getCourseId());
        dto.setIsCompleted(String.valueOf(course.isCompleted()));

        List<EnrolledModuleDto> moduleDto = course.getEnrolledModules().stream()
                .map(EnrolledCourseMapper::mapModule)
                .collect(Collectors.toList());
        dto.setEnrollModuleDto(moduleDto);

        return dto;
    }

    private static EnrolledModuleDto mapModule(EnrolledModules module) {
        EnrolledModuleDto dto = new EnrolledModuleDto();
        dto.setId(module.getId());
        dto.setModuleName(module.getModuleName());
        dto.setDuration(module.getDuration());
        dto.setModuleId(module.getModuleId());
        dto.setCompleted(module.isCompleted());
        return dto;
    }
}
