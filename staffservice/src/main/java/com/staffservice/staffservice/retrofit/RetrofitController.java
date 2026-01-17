package com.staffservice.staffservice.retrofit;

import com.shared.dtos.AssignmentDto;
import com.shared.dtos.CatAssessmentResponseDto;
import com.shared.dtos.QuizAssessmentResponseDto;
import com.shared.dtos.SubmissionGradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class RetrofitController {
    private final RetrofitService retrofitService;
 @GetMapping("/course/get/quizAssessment")
 private ResponseEntity<QuizAssessmentResponseDto> getQuizAssessment
         (@RequestParam Integer courseId,@RequestParam Integer moduleId, @RequestParam Integer quizId) {
     return ResponseEntity.ok(retrofitService.getQuizAssessment(courseId,moduleId,quizId));

 }
    @GetMapping("/course/get/catAssessment")
    private ResponseEntity<CatAssessmentResponseDto> getCatAssessment
            (@RequestParam Integer courseId, @RequestParam Integer catId) {
        return ResponseEntity.ok(retrofitService.getCatAssessment(courseId,catId));

    }
    @GetMapping("/grades")
    public ResponseEntity<List<SubmissionGradeDto>> getFilteredGrades(
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String submissionType,
            @RequestParam(required = false) String studentAdmissionId,
            @RequestParam(required = false) String className
    ) {
        List<SubmissionGradeDto> grades =retrofitService.getFilteredSubmissionGrades(
                courseId, submissionType, studentAdmissionId, className
        );
        return ResponseEntity.ok(grades);
    }
    @GetMapping("/grades/submissionid")
    public ResponseEntity<SubmissionGradeDto> getSubmissionGrades(@RequestParam Long submissionId) {
     return ResponseEntity.ok(retrofitService.getSubmissionGradesBySubmissionId(submissionId));
    }
    @GetMapping("/get/assignments")
    public ResponseEntity<AssignmentDto> getAssignments(@RequestParam Long assignmentId , @RequestParam Integer courseId) {
        return ResponseEntity.ok(retrofitService.getAssignments(assignmentId,courseId));
    }




}
