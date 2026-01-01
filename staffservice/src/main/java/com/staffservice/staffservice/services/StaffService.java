package com.staffservice.staffservice.services;
import com.shared.dtos.*;
import com.staffservice.staffservice.exceptions.MissingFieldException;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staffservice.staffservice.configuration.RabbitMQConfiguration;
import com.staffservice.staffservice.dtos.*;
import com.staffservice.staffservice.entities.*;
import com.staffservice.staffservice.entities.Class;
import com.staffservice.staffservice.exceptions.UserNotFoundException;
import com.staffservice.staffservice.repositories.AssignmentRepository;
import com.staffservice.staffservice.repositories.ClassRepository;
import com.staffservice.staffservice.repositories.StaffRepository;
import com.staffservice.staffservice.repositories.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    private final Logger log = LoggerFactory.getLogger(StaffService.class);
    private final AssignmentRepository assignmentRepository;
    private final ClassRepository classRepository;
    private final SubmissionRepository submissionRepository;


    public List<StaffDto> getAllStaffMembers() {

        List<Staff> staffList = staffRepository.findAll();
        return staffList.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Page<StaffDto> getFilteredStaff(Long staffId, Long admissionYear, String department, Pageable pageable) {
        Page<Staff> staffPage = staffRepository.findByFilters(staffId, admissionYear, department, pageable);
        return staffPage.map(this::mapToDto);
    }


    public ResponseEntity<StaffDto> getStaffByStaffId(Long staffID) {

        Optional<Staff> staffOptional = staffRepository.findById(staffID);

        if (staffOptional.isPresent()) {
            Staff staff = staffOptional.get();
            StaffDto staffDto = mapToDto(staff);
            return ResponseEntity.ok(staffDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public String deleteStaffByID(Long staffId) {
        Optional<Staff> staffOptional = staffRepository.findById(staffId);
        if (staffOptional.isPresent()) {
            staffRepository.delete(staffOptional.get());
            return "Staff deleted successfully";
        } else {
            throw new UserNotFoundException("Staff not found with ID: " + staffId);
        }
    }

    private StaffDto mapToDto(Staff staff) {
        return new StaffDto(
                staff.getFirstName(),
                staff.getStaffId(),
                staff.getLastName(),
                staff.getEmail(),
                staff.getDepartment(),
                staff.getGender(),
                staff.getPhoneNumber(),
                staff.getBirthYear(),
                staff.getAdmissionYear(),
                staff.getRole()
        );
    }

    @RabbitListener(queues = RabbitMQConfiguration.ADD_STAFF_QUEUE)
    public void addStaff(StaffPayload staffDto) {
        Staff staff = new Staff();
        staff.setRole(Role.STAFF);
        staff.setDepartment(staffDto.getDepartment());
        staff.setGender(staffDto.getGender());
        staff.setAdmissionYear(staffDto.getAdmissionYear());
        staff.setFirstName(staffDto.getFirstName());
        staff.setLastName(staffDto.getLastName());
        staff.setBirthYear(staffDto.getBirthYear());
        staff.setPhoneNumber(staffDto.getPhoneNumber());
        staff.setEmail(staffDto.getEmail());
        log.info("Staff added successfully: {}", staffDto);
        staffRepository.save(staff);

    }

    public String createAssignment(AssignmentRequest request, Principal principal) {
        String email = principal.getName();
        Optional<Staff> staff = staffRepository.findByEmail(email);
        Assignments assignment = new Assignments();
        assignment.setTitle(request.getTitle());
        assignment.setStaffId(staff.get().getStaffId());
        assignment.setDescription(request.getDescription());
        assignment.setCreationDate(LocalDateTime.now());
        assignment.setDueDate(LocalDateTime.parse(request.getDueDate()));

        int totalMarks = request.getQuestions()
                .stream()
                .mapToInt(QuestionRequest::getMarks)
                .sum();
        assignment.setMarks(totalMarks);

        Set<Class> classes = classRepository.findByClassNameIn(request.getClasses());
        assignment.setClasses(classes);

        List<Questions> questions = request.getQuestions().stream().map(q -> {
            Questions question = new Questions();
            question.setQuestionText(q.getQuestionText());
            question.setQuestionType(QuestionType.valueOf(q.getType()));
            question.setOptions(new HashSet<>(q.getOptions()));
            question.setMarks(q.getMarks());
            question.setCorrectAnswer(q.getCorrectAnswer());
            question.setAssignment(assignment);
            return question;
        }).collect(Collectors.toList());

        assignment.setQuestions(questions);

        assignmentRepository.save(assignment);
        return "Assignment created successfully";
    }

    public List<ClassDto> getAllClasses() {
        List<Class> classes = classRepository.findAll();
        return classes.stream()
                .map(d -> {
                    ClassDto dto = new ClassDto();
                    dto.setClasId(d.getClassId());
                    dto.setClassName(d.getClassName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
  @Transactional(readOnly = true)
    public List<AssignmentDto> getAllAssignments(String className) {
        List<Assignments> assignments = assignmentRepository.findByClasses_ClassName(className);
        return assignments.stream()
                .map(AssignmentMapper::toDto)
                .toList();
    }

    @RabbitListener(queues = RabbitMQConfiguration.ADD_ASSIGNMENT_QUEUE)
    public void addSubmission(SubmissionRequestDto submissionRequestDto) {
        Optional<Submission> submissionOpt =
                submissionRepository.findByStudentAdmissionIdAndAssignmentId(
                        submissionRequestDto.getAdmissionId(),
                        submissionRequestDto.getAssignmentId()
                );

        try {
            String jsonAnswers = objectMapper.writeValueAsString(submissionRequestDto.getAnswerText());

            if (submissionOpt.isPresent()) {
                Submission existingSubmission = submissionOpt.get();
                existingSubmission.setSubmissionText(jsonAnswers);
                existingSubmission.setSubmissionDate(LocalDateTime.now().toString());
                submissionRepository.save(existingSubmission);
            } else {
                Submission submission = new Submission();
                submission.setClassName(submissionRequestDto.getClassName());
                submission.setSubmissionText(jsonAnswers);
                submission.setSubmissionStatus(SubmissionStatus.UNGRADED);
                submission.setSubmitted(true);
                submission.setStudentAdmissionId(submissionRequestDto.getAdmissionId());
                submission.setAssignmentId(submissionRequestDto.getAssignmentId());
                submission.setSubmissionDate(LocalDateTime.now().toString());
                submissionRepository.save(submission);
                log.info("SubmissionRequest loaded: {}", submissionRequestDto);
                log.info("Submission added successfully: {}", submission);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save submission", e);
        }
    }


    public List<SubmissionDto> getSubmissions(String studentAdmissionId) {
        return submissionRepository.findByStudentAdmissionId(studentAdmissionId).stream().map(this::mapToDto).toList();

    }
    @Transactional(readOnly = true)
    public List<AssignmentDto> getAssignmentsByStaffId(Long staffId) {

        List<Assignments> assignments = assignmentRepository.findByStaffId(staffId);
        return assignments.stream()
                .map(AssignmentMapper::toDto)
                .toList();

    }

    public StaffDto getActiveStaff(Principal principal) {
        String email = principal.getName();
        Staff staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Staff not found with email: " + email));

        return mapToDto(staff);
    }

    public List<SubmissionDto> getSubmissionsByAssignmentId(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId).stream().map(this::mapToDto).toList();
    }

    public List<SubmissionDto> getAllUngradedSubmissions() {
        return submissionRepository.findAllBySubmissionStatus(SubmissionStatus.UNGRADED).stream().map(this::mapToDto).toList();
    }

    public void sendSubmissionForGrading() {
        List<SubmissionDto> dto = getAllUngradedSubmissions();
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.GRADING_REQUEST_QUEUE, dto);
    }

    private SubmissionDto mapToDto(Submission sub) {
        SubmissionDto dto = new SubmissionDto();
        dto.setSubmissionId((long) sub.getSubmissionId());
        dto.setAssignmentId(sub.getAssignmentId());
        dto.setClassName(sub.getClassName());
        dto.setStudentAdmissionId(sub.getStudentAdmissionId());
        dto.setSubmissionDate(LocalDateTime.parse(sub.getSubmissionDate()));
        dto.setSubmissionStatus(String.valueOf(sub.getSubmissionStatus()));
        dto.setSubmitted(sub.isSubmitted());

        try {
            if (sub.getSubmissionText() != null) {
                List<AnswerDto> answers = objectMapper.readValue(
                        sub.getSubmissionText(),
                        new TypeReference<List<AnswerDto>>() {
                        }
                );
                dto.setAnswers(answers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }


    public String assignCourses(AssignCourseDto assignCourseDto) {
        if (assignCourseDto == null) {
            return "AssignCourseDto cannot be null";
        }

        rabbitTemplate.convertAndSend(RabbitMQConfiguration.ASSIGN_COURSES, assignCourseDto);

        return "Classnames assigned successfully:" + assignCourseDto.getClassName();
    }

}

