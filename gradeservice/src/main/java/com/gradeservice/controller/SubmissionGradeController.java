package com.gradeservice.controller;

import com.gradeservice.dtos.SubmissionGradeDto;
import com.gradeservice.services.SubmissionGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grade")
public class SubmissionGradeController {
    private final SubmissionGradeService submissionGradeService;

    @GetMapping("/filter/grades")
    public List<SubmissionGradeDto> filterGrades(
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String submissionType,
            @RequestParam(required = false) String studentAdmissionId,
            @RequestParam(required = false) String className
    ) {
        return submissionGradeService.filterGrades(
                courseId, submissionType, studentAdmissionId, className);
    }
}
