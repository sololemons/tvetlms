package com.studentservice.student.controllers;


import com.shared.dtos.SubmissionRequestDto;
import com.studentservice.student.dtos.EnrollDto;
import com.studentservice.student.dtos.ProfileDto;
import com.studentservice.student.dtos.StudentDto;
import com.studentservice.student.services.StudentServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentServices studentServices;


    @GetMapping("/get/filter")
    public ResponseEntity<Page<StudentDto>> getStudents(
            @RequestParam(value = "AdmissionId", required = false) Long admissionId,
            @RequestParam(value = "AdmissionYear", required = false) Integer admissionYear,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<StudentDto> students = studentServices.getFilteredStudents(admissionId, admissionYear, pageable);
        return ResponseEntity.ok(students);
    }




    @GetMapping("/all")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(studentServices.getAllStudents());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentServices.getStudentByAdmissionId(id));
    }


    @PutMapping("/update")
    public ResponseEntity<StudentDto> updateStudent(@RequestBody StudentDto student) {
        return  ResponseEntity.ok(studentServices.updateStudent(student));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentServices.deleteStudent(id));
    }

    @GetMapping("/get/profile")
    public ResponseEntity<StudentDto> getStudent(Principal principal) {
        return ResponseEntity.ok(studentServices.getActiveStudent(principal));
    }
    @PostMapping("/add/submission")
    public ResponseEntity<String> submitAssignment(@RequestBody SubmissionRequestDto submissionRequestDto) {
        return ResponseEntity.ok(studentServices.submitAssignment(submissionRequestDto));
    }
    @PostMapping("/enroll/course")
    public ResponseEntity<String> enrollCourses(@RequestBody EnrollDto enrollDto){
        return ResponseEntity.ok(studentServices.enrollCourses(enrollDto));
    }
    @PostMapping ("/set/module/complete/{moduleId}")
    public ResponseEntity<String> setModuleComplete(@PathVariable Long moduleId,Principal principal){
        return ResponseEntity.ok(studentServices.setModuleDone(moduleId,principal));
    }
    @PostMapping("/complete/profile")
    public ResponseEntity<String>completeProfile(@RequestBody ProfileDto profileDto,Principal principal){
        return ResponseEntity.ok(studentServices.completeProfile(profileDto,principal));
    }







}
