package com.coursemanagement.controllers;

import com.coursemanagement.dtos.*;
import com.shared.dtos.*;
import com.coursemanagement.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/course")
@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/get/all")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
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

    @PutMapping("/update/course/info")
    public ResponseEntity<String> updateBasicCourseInfo(@RequestBody UpdateCourseDto updateCourseDto) {
        return ResponseEntity.ok(courseService.updateCourseInfo(updateCourseDto));
    }

    @PostMapping("/add/quiz/assessment")
    public ResponseEntity<String> generateQuizQuestions(@RequestBody QuizGenerationRequest quizGenerationRequest) {
        return ResponseEntity.ok(courseService.generateQuiz(quizGenerationRequest));
    }

    @PostMapping("/add/cat/assessment")
    public ResponseEntity<String> generateCatAssessment(@RequestBody CatGenerationRequest catGenerationRequest) {
        return ResponseEntity.ok(courseService.generateCatAssessment(catGenerationRequest));
    }

    @PostMapping("/create/course")
    public ResponseEntity<String> createCourse(@RequestBody CourseDto courseDto) {
        return ResponseEntity.ok(courseService.createCourse(courseDto));
    }

    @PostMapping("/create/module")
    public ResponseEntity<String> createModule(@RequestBody CreateModuleDto createModuleDto) {
        return ResponseEntity.ok(courseService.createModule(createModuleDto));
    }

    @PostMapping("/create/cat")
    public ResponseEntity<String> createCatAssessment(@RequestBody CreateCatDto createCatDto) {
        return ResponseEntity.ok(courseService.createCat(createCatDto));
    }

    @PostMapping("/create/quiz")
    public ResponseEntity<String> createQuizAssessment(@RequestBody CreateQuizDto createQuizDto) {
        return ResponseEntity.ok(courseService.createQuiz(createQuizDto));
    }

    @PostMapping("/create/assignment")
    public ResponseEntity<String> createAssignment(@RequestBody CreateAssignmentDto createAssignmentDto) {
        return ResponseEntity.ok(courseService.createAssignment(createAssignmentDto));
    }

    @GetMapping("/get/course/{courseId}")
    public ResponseEntity<CourseDto> getFullCourse(@PathVariable Integer courseId) {
        return ResponseEntity.ok(courseService.getFullCourse(courseId));
    }

    @GetMapping("/get/course/active/{courseId}")
    public ResponseEntity<CourseDto> getActiveCourse(@PathVariable Integer courseId) {
        return ResponseEntity.ok(courseService.getActiveCourses(courseId));
    }

    @PostMapping("/mark/module/active")
    public ResponseEntity<String> activateModule(@RequestBody ActivateModuleDto activateModuleDto) {
        return ResponseEntity.ok(courseService.activateModule(activateModuleDto));
    }

    @GetMapping("/get/quizAssessment")
    public ResponseEntity<QuizAssessmentResponseDto> getQuizAssessment
            (@RequestParam Integer courseId, @RequestParam Integer moduleId, @RequestParam Integer quizId) {
        return ResponseEntity.ok(courseService.getQuizAssessmentDto(courseId, moduleId, quizId));
    }
    @GetMapping("/get/catAssessment")
    public ResponseEntity<CatAssessmentResponseDto> getCatAssessment
            (@RequestParam Integer courseId, @RequestParam Integer catId){
        return ResponseEntity.ok(courseService.getCatAssessment(courseId,catId));
    }
    @GetMapping("/get/assignments")
    public ResponseEntity<AssignmentDto>getAssignment(@RequestParam Long assignmentId , @RequestParam Integer courseId){
        return ResponseEntity.ok(courseService.getAssignment(assignmentId,courseId));

    }

}


