package com.studentservice.student.controllers;


import com.shared.dtos.AssignmentSubmissionDto;
import com.shared.dtos.NotificationDto;
import com.shared.dtos.SubmissionDto;
import com.shared.dtos.SubmissionRequestDto;
import com.studentservice.student.dtos.*;
import com.studentservice.student.services.StudentServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/enroll/course")
    public ResponseEntity<String> enrollCourses(@RequestBody EnrollDto enrollDto){
        return ResponseEntity.ok(studentServices.enrollCourses(enrollDto));
    }
    @PostMapping ("/set/module/complete")
    public ResponseEntity<String> setModuleComplete(@RequestBody MarkModuleDoneDto markModuleDoneDto, Principal principal){
        return ResponseEntity.ok(studentServices.setModuleDone(markModuleDoneDto,principal));
    }
    @PostMapping("/complete/profile")
    public ResponseEntity<String>completeProfile(@RequestBody ProfileDto profileDto,Principal principal){
        return ResponseEntity.ok(studentServices.completeProfile(profileDto,principal));
    }
    @GetMapping("/get/enrolled/courses")
    public  ResponseEntity<List<EnrolledCoursesDto>>fetchEnrolledCourses(Principal principal){
        return ResponseEntity.ok(studentServices.fetchAllEnrolledCourses(principal));
    }
    @GetMapping("/get/gamify/profiles")
    public ResponseEntity<List<GamifyProfilesDto>> fetchGamifyProfiles(){
        return ResponseEntity.ok(studentServices.fetchGamifyProfiles());
    }
    @PostMapping(
            value = "/submit/assignment",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> submitAssignment(
            @RequestPart("metadata") SubmissionAssignmentDto dto,
            @RequestPart("files") List<MultipartFile> files,
            Principal principal
    ) {
        return ResponseEntity.ok(
                studentServices.submitAssignment(dto, files,principal)
        );
    }
    @PostMapping("/submit/cat")
    public ResponseEntity<String> submitCat(@RequestBody SubmissionCatDto submissionCatDto ,Principal principal){
        return ResponseEntity.ok(studentServices.submitCat(submissionCatDto,principal));
    }
    @PostMapping("/submit/quiz")
    public ResponseEntity<String> submitQuiz(@RequestBody SubmissionQuizDto submissionQuizDto  ,Principal principal){
        return ResponseEntity.ok(studentServices.submitQuiz(submissionQuizDto,principal));
    }
    @GetMapping("/get/notifications")
    public ResponseEntity<List<NotificationDto>> getNotifications(Principal principal){
        return ResponseEntity.ok(studentServices.getNotifications(principal));
    }
    @GetMapping("/view/graded/submissions")
    public ResponseEntity<>









}
