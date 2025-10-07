package com.coursemanagement.controllers;

import com.coursemanagement.dtos.CourseDto;
import com.shared.dtos.ModuleDto;
import com.coursemanagement.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/course")
@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/get/all")
    public ResponseEntity<List<CourseDto>>getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }
    @PostMapping("/add")
    public ResponseEntity<String> addCourse(@RequestBody CourseDto courseDto) {
        return ResponseEntity.ok(courseService.addCourses(courseDto));
    }
    @GetMapping("/get/module/{courseId}")
    public ResponseEntity<List<ModuleDto>> getModule(@PathVariable Integer courseId) {
     return ResponseEntity.ok(courseService.getModules(courseId));
    }
}
