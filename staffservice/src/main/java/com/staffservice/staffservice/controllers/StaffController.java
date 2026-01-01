package com.staffservice.staffservice.controllers;

import com.shared.dtos.AssignCourseDto;
import com.shared.dtos.SubmissionDto;
import com.staffservice.staffservice.dtos.*;
import com.staffservice.staffservice.services.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffServices;


    @GetMapping("/all")
    public ResponseEntity<List<StaffDto>> getAllStaff() {

        return ResponseEntity.ok(staffServices.getAllStaffMembers());
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<StaffDto>> getStaff(
            @RequestParam(value = "staffId", required = false) Long staffId,
            @RequestParam(value = "admissionYear", required = false) Long admissionYear,
            @RequestParam(value = "department", required = false) String department,
            Pageable pageable) {

        Page<StaffDto> staff = staffServices.getFilteredStaff(staffId, admissionYear, department, pageable);
        return ResponseEntity.ok(staff);
    }


    @GetMapping("/id/{staffId}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable Long staffId) {

        return staffServices.getStaffByStaffId(staffId);
    }


    @DeleteMapping("delete/{staffId}")
    public ResponseEntity<String> deleteStaffById(@PathVariable Long staffId) {

        return ResponseEntity.ok(staffServices.deleteStaffByID(staffId));
    }

    @PostMapping("/create/assignment")
    public ResponseEntity<String> createAssignment(@RequestBody AssignmentRequest request, Principal principal) {

        return ResponseEntity.ok(staffServices.createAssignment(request, principal));
    }

    @GetMapping("/get/classes")
    public ResponseEntity<List<ClassDto>> getAllClasses() {
        return ResponseEntity.ok(staffServices.getAllClasses());
    }

    @GetMapping("/view/assignments")
    public ResponseEntity<List<AssignmentDto>> getAssignments(@RequestParam String className) {
        return ResponseEntity.ok(staffServices.getAllAssignments(className));
    }

    @GetMapping("/view/submissions")
    public ResponseEntity<List<SubmissionDto>> getSubmissions(@RequestParam String studentAdmissionId) {
        return ResponseEntity.ok(staffServices.getSubmissions(studentAdmissionId));
    }

    @GetMapping("/get/assignments/staffId")
    public ResponseEntity<List<AssignmentDto>> getSubmissionsByStaffId(@RequestParam Long staffId) {
        return ResponseEntity.ok(staffServices.getAssignmentsByStaffId(staffId));
    }

    @GetMapping("/get/profile")
    public ResponseEntity<StaffDto> getStaff(Principal principal) {
        return ResponseEntity.ok(staffServices.getActiveStaff(principal));
    }
    @GetMapping("/get/submissions/assignmentId")
    public ResponseEntity<List<SubmissionDto>> getSubmissionsByAssignmentId(@RequestParam Long assignmentId) {
        return ResponseEntity.ok(staffServices.getSubmissionsByAssignmentId(assignmentId));
    }
    @GetMapping("/get/ungraded/submissions")
    public ResponseEntity<List<SubmissionDto>> getUngradedSubmissions() {
        return ResponseEntity.ok(staffServices.getAllUngradedSubmissions());
    }
    @PostMapping("/assign/course")
    public ResponseEntity<String> assignCourses(@RequestBody AssignCourseDto assignCourseDto){
        return ResponseEntity.ok(staffServices.assignCourses(assignCourseDto));

    }



}
