package com.staffservice.staffservice.controllers;

import com.shared.dtos.AssignmentGradeResponseDto;
import com.shared.dtos.AssignmentSubmissionResponseDto;
import com.shared.dtos.GradedAssignmentDetailsDto;
import com.staffservice.staffservice.dtos.AssignmentGradeRequestDto;
import com.staffservice.staffservice.services.ViewSubmissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class ViewSubmissionsController {

    private final ViewSubmissionsService viewSubmissionsService;

    @GetMapping("/get/all/ungraded/assignment/submissions")
    public ResponseEntity<List<AssignmentSubmissionResponseDto>> getAllAssignmentSubmissions(@RequestParam Long assignmentId) {
        return ResponseEntity.ok(viewSubmissionsService.getAssignmentSubmissions(assignmentId));
    }
    @GetMapping("/submissions/by/submissionid")
    public ResponseEntity <AssignmentSubmissionResponseDto> getAssignmentSubmissionById(
            @RequestParam Long submissionId
    ) {
        return ResponseEntity.ok(viewSubmissionsService.getAssignmentSubmissionById(submissionId));
    }
    @PostMapping("/submission/grade/assignments")
    public ResponseEntity<AssignmentGradeResponseDto> gradeAssignment(

            @RequestBody AssignmentGradeRequestDto request
    ) {
        return ResponseEntity.ok(viewSubmissionsService.gradeAssignments(request));
    }
    @GetMapping("/get/graded/submissions/assignments")
    public ResponseEntity<GradedAssignmentDetailsDto>
    getAssignmentSubmission(@RequestParam Long submissionId) {

        return ResponseEntity.ok(
                viewSubmissionsService.getGradedAssignments(submissionId)
        );
    }




}
