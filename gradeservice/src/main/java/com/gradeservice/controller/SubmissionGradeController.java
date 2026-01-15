package com.gradeservice.controller;

import com.gradeservice.services.SubmissionGradeService;
import com.shared.dtos.SubmissionGradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String submissionId

    ) {
        return submissionGradeService.filterGrades(
                courseId, submissionType, studentAdmissionId, className, submissionId);
    }
    @GetMapping("/get/grades/submissionid")
    public ResponseEntity<SubmissionGradeDto> getSubmissionGrade(@RequestParam String submissionId) {
        return ResponseEntity.ok(submissionGradeService.getSubmissionGrades(submissionId));
    }
}
