package com.coursemanagement.services;

import com.coursemanagement.dtos.CourseDto;
import com.coursemanagement.dtos.ModuleDto;
import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.CourseModule;
import com.coursemanagement.repository.CourseRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map(this::mapToDto).toList();
    }
    private CourseDto mapToDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setCourseName(course.getCourseName());
        courseDto.setDescription(course.getDescription());
        courseDto.setCourseOverview(course.getCourseOverview());

        if (course.getModules() != null) {
            List<ModuleDto> moduleDto = course.getModules().stream()
                    .map(module -> new ModuleDto(
                            module.getWeek(),
                            module.getModuleName(),
                            module.getContent()
                    ))
                    .toList();
            courseDto.setModuleDto(moduleDto);
        }

        return courseDto;
    }

    public String addCourses(CourseDto courseDto) {
        Course course = new Course();
        course.setCourseName(courseDto.getCourseName());
        course.setDescription(courseDto.getDescription());
        course.setCourseOverview(courseDto.getCourseOverview());

        if (courseDto.getModuleDto() != null) {
            List<CourseModule> modules = courseDto.getModuleDto().stream()
                    .map(dto -> {
                        CourseModule module = new CourseModule();
                        module.setWeek(dto.getWeek());
                        module.setModuleName(dto.getModuleName());
                        module.setContent(dto.getContent());
                        module.setCourse(course);
                        return module;
                    })
                    .toList();
            course.setModules(modules);
        }

        courseRepository.save(course);
        return "Course added successfully!";
    }


}
