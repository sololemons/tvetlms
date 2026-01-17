package com.staffservice.staffservice.services;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.shared.dtos.*;
import com.staffservice.staffservice.repositories.*;
import com.staffservice.staffservice.retrofit.RetrofitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staffservice.staffservice.configuration.RabbitMQConfiguration;
import com.staffservice.staffservice.dtos.*;
import com.staffservice.staffservice.entities.*;
import com.staffservice.staffservice.entities.Class;
import com.staffservice.staffservice.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {
    private final StaffRepository staffRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    private final AssignmentRepository assignmentRepository;
    private final ClassRepository classRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionFileRepository submissionFileRepository;
    private final RetrofitService retrofitService;


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


   /*
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
              //  submission.setAssignmentId(submissionRequestDto.getAssignmentId());
                submission.setSubmissionDate(LocalDateTime.now().toString());
                submissionRepository.save(submission);
                log.info("SubmissionRequest loaded: {}", submissionRequestDto);
                log.info("Submission added successfully: {}", submission);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save submission", e);
        }
    }
*/


    public List<SubmissionDto> getSubmissions(String studentAdmissionId) {
        return submissionRepository.findByStudentAdmissionId(studentAdmissionId).stream().map(this::mapToDto).toList();

    }



    public StaffDto getActiveStaff(Principal principal) {
        String email = principal.getName();
        Staff staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Staff not found with email: " + email));

        return mapToDto(staff);
    }

    /*
       public List<SubmissionDto> getSubmissionsByAssignmentId(Long assignmentId) {
            return submissionRepository.findByAssignmentId(assignmentId).stream().map(this::mapToDto).toList();
        }
    */
    public List<SubmissionDto> getAllUngradedSubmissions() {
        return submissionRepository.findAllBySubmissionStatus(SubmissionStatus.UNGRADED).stream().map(this::mapToDto).toList();
    }

    public void sendSubmissionForGrading() {
        List<SubmissionDto> dto = getAllUngradedSubmissions();
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.GRADING_REQUEST_QUEUE, dto);
    }

    private SubmissionDto mapToDto(Submission sub) {
        SubmissionDto dto = new SubmissionDto();
        dto.setSubmissionId((long) sub.getId());
        //   dto.setAssignmentId(sub.getAssignmentId());
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

    @RabbitListener(queues = RabbitMQConfiguration.ADD_ASSIGNMENT_QUEUE)
    @Transactional
    public void consumeAssignmentSubmission(AssignmentSubmissionDto event) {

        log.info("Received Submission {}", event);

        Submission submission = new Submission();
        submission.setStudentAdmissionId(event.getStudentAdmissionId());
        submission.setSubmissionType(event.getSubmissionType());
        submission.setTargetId(event.getAssignmentId());
        submission.setCourseId(event.getCourseId());
        submission.setClassName(event.getClassName());
        submission.setSubmissionDate(String.valueOf(event.getSubmissionDate()));
        submission.setSubmissionStatus(SubmissionStatus.UNGRADED);
        submission.setSubmitted(true);

        log.info("Assignment submission created successfully of Id : {}", submission.getId());

        Submission saved = submissionRepository.save(submission);

        SubmissionFile file = new SubmissionFile();
        file.setFileName(event.getFileName());
        file.setFileType(event.getFileType());
        file.setFileUrl(event.getFileUrl());
        file.setSubmission(saved);

        log.info("Submission File saved {} with filename", file.getFileName());

        submissionFileRepository.save(file);
    }

    @RabbitListener(queues = RabbitMQConfiguration.ADD_CAT_SUBMISSION_QUEUE)
    public void addCatSubmission(CatSubmissionDto submissionDto) {

        Optional<Submission> submissionOpt =
                submissionRepository.findByStudentAdmissionIdAndTargetIdAndSubmissionTypeAndCourseId(
                        submissionDto.getStudentAdmissionId(),
                        (long) submissionDto.getCatId(),
                        SubmissionType.CAT,
                        submissionDto.getCourseId()
                );

        try {
            // Convert structured answers to JSON
            String jsonAnswers =
                    objectMapper.writeValueAsString(submissionDto.getAnswerText());

            if (submissionOpt.isPresent()) {

                Submission existing = submissionOpt.get();
                existing.setSubmissionText(jsonAnswers);
                existing.setSubmissionDate(String.valueOf(LocalDateTime.now()));
                submissionRepository.save(existing);

            } else {

                Submission submission = new Submission();
                submission.setStudentAdmissionId(submissionDto.getStudentAdmissionId());
                submission.setClassName(submissionDto.getClassName());
                submission.setSubmissionType(SubmissionType.CAT);
                submission.setTargetId((long) submissionDto.getCatId());
                submission.setCourseId(submissionDto.getCourseId());
                submission.setSubmissionText(jsonAnswers);
                submission.setSubmissionStatus(SubmissionStatus.UNGRADED);
                submission.setSubmitted(true);
                submission.setSubmissionDate(String.valueOf(LocalDateTime.now()));

                submissionRepository.save(submission);

                log.info("CAT submission saved: {} from course with id {} and Cat Id {} ", submission,
                        submissionDto.getCourseId(), submissionDto.getCatId());
            }

        } catch (Exception e) {
            log.error("Failed to save CAT submission", e);
            throw new RuntimeException("Failed to save CAT submission", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.ADD_QUIZ_SUBMISSION_QUEUE)
    public void addQuizSubmission(QuizSubmissionDto submissionDto) {

        Optional<Submission> submissionOpt =
                submissionRepository.findByStudentAdmissionIdAndTargetIdAndSubmissionTypeAndCourseId(
                        submissionDto.getStudentAdmissionId(),
                        (long) submissionDto.getQuizId(),
                        SubmissionType.CAT,
                        submissionDto.getCourseId()
                );

        try {
            // Convert structured answers to JSON
            String jsonAnswers =
                    objectMapper.writeValueAsString(submissionDto.getAnswerText());

            if (submissionOpt.isPresent()) {

                Submission existing = submissionOpt.get();
                existing.setSubmissionText(jsonAnswers);
                existing.setSubmissionDate(String.valueOf(LocalDateTime.now()));
                submissionRepository.save(existing);

            } else {

                Submission submission = new Submission();
                submission.setStudentAdmissionId(submissionDto.getStudentAdmissionId());
                submission.setClassName(submissionDto.getClassName());
                submission.setSubmissionType(SubmissionType.QUIZ);
                submission.setTargetId((long) submissionDto.getQuizId());
                submission.setCourseId(submissionDto.getCourseId());
                submission.setModuleId(submissionDto.getModuleId());
                submission.setSubmissionText(jsonAnswers);
                submission.setSubmissionStatus(SubmissionStatus.UNGRADED);
                submission.setSubmitted(true);
                submission.setSubmissionDate(String.valueOf(LocalDateTime.now()));

                submissionRepository.save(submission);

                log.info("Quiz submission saved: {} for Course with id: {} and module Id: {} ", submission, submissionDto.getCourseId(), submissionDto.getModuleId());
            }

        } catch (Exception e) {
            log.error("Failed to save Quiz submission", e);
            throw new RuntimeException("Failed to save Quiz submission", e);
        }
    }


    public String createNotification(NotificationRequestDto notificationRequestDto) {
        if (notificationRequestDto != null) {
            notificationRequestDto.setDate(LocalDateTime.now());
            rabbitTemplate.convertAndSend(RabbitMQConfiguration.ADD_NOTIFICATIONS, notificationRequestDto);
        }
        return "Notification Sent From Staff Service";
    }


    public SubmissionUngradedViewDto getUngradedAssessmentByStudent(Long submissionId) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        // 1️⃣ Parse student answers
        List<SubmissionAnswerDto> studentAnswers =
                parseSubmissionAnswers(submission.getSubmissionText());

        Map<Integer, String> studentAnswerMap =
                studentAnswers.stream()
                        .collect(Collectors.toMap(
                                SubmissionAnswerDto::getQuestionId,
                                SubmissionAnswerDto::getAnswerText
                        ));

        // 2️⃣ Fetch assessment (QUIZ or CAT)
        AssessmentResponse assessment = switch (submission.getSubmissionType()) {

            case QUIZ -> retrofitService.getQuizAssessment(
                    submission.getCourseId(),
                    submission.getModuleId(),
                    (int) submission.getTargetId()
            );

            case CAT -> retrofitService.getCatAssessment(
                    submission.getCourseId(),
                    (int) submission.getTargetId()
            );

            default -> throw new IllegalStateException(
                    "Unsupported submission type: " + submission.getSubmissionType()
            );
        };

        // 3️⃣ Merge questions + student answers
        List<SubmissionUngradedQuizQuestionViewDto> merged =
                assessment.getQuestions().stream()
                        .map(q -> new SubmissionUngradedQuizQuestionViewDto(
                                q.getQuestionId(),
                                q.getText(),
                                q.getOptions(),
                                studentAnswerMap.get(q.getQuestionId()),
                                q.getCorrectAnswer(),
                                q.getMarks()
                        ))
                        .toList();

        // 4️⃣ Final response
        return new SubmissionUngradedViewDto(
                submission.getId(),
                submission.getStudentAdmissionId(),
                submission.getTargetId(),
                assessment.getTitle(),
                merged
        );
    }

    private List<SubmissionAnswerDto> parseSubmissionAnswers(String json) {
        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<SubmissionAnswerDto>>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid submission JSON", e);
        }
    }

    @Transactional
    public SubmissionViewDto getGradedAssessmentByStudent(Long submissionId) {

        Submission submission = submissionRepository
                .findByIdAndSubmissionStatus(submissionId, SubmissionStatus.GRADED)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        log.info("Submission /n {}", submission);

        List<SubmissionAnswerDto> studentAnswers =
                parseSubmissionAnswers(submission.getSubmissionText());
        log.info("student answers {}",studentAnswers);



        Map<String, Integer> keyToQuestionIdMap;
        try {
            keyToQuestionIdMap = objectMapper.readValue(
                    submission.getQuestionKeyMapJson(),
                    new TypeReference<Map<String, Integer>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse questionKeyMapJson", e);
        }
        log.info("keyToQuestionIdMap {}",keyToQuestionIdMap);


        // 3️⃣ Invert map: questionId -> key
        Map<Integer, String> questionIdToKeyMap =
                keyToQuestionIdMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getValue,
                                Map.Entry::getKey
                        ));
        log.info("questionIdToKeyMap {}",questionIdToKeyMap);

        Map<String, String> studentAnswerMap = studentAnswers.stream()
                .collect(Collectors.toMap(
                        a -> {
                            String key = questionIdToKeyMap.get(a.getQuestionId());
                            if (key == null) {
                                throw new IllegalStateException(
                                        "No question key for questionId=" + a.getQuestionId()
                                );
                            }
                            return key;
                        },
                        SubmissionAnswerDto::getAnswerText
                ));

        log.info("studentAnswerMap {}",studentAnswerMap);

        AssessmentResponse assessment = switch (submission.getSubmissionType()) {
            case QUIZ -> retrofitService.getQuizAssessment(
                    submission.getCourseId(),
                    submission.getModuleId(),
                    (int) submission.getTargetId()
            );
            case CAT -> retrofitService.getCatAssessment(
                    submission.getCourseId(),
                    (int) submission.getTargetId()
            );
            default -> throw new IllegalStateException(
                    "Unsupported submission type: " + submission.getSubmissionType()
            );
        };

        SubmissionGradeDto submissionGradeDto =
                retrofitService.getSubmissionGradesBySubmissionId(submission.getId());

        Map<String, QuestionGradeDto> gradeMap =
                submissionGradeDto.getQuestionGrades().stream()
                        .collect(Collectors.toMap(
                                QuestionGradeDto::getQuestionId,
                                g -> g
                        ));

        List<SubmissionQuizQuestionViewDto> merged =
                assessment.getQuestions().stream()
                        .map(q -> {
                            String qKey = questionIdToKeyMap.get(q.getQuestionId());
                            QuestionGradeDto grade = qKey != null ? gradeMap.get(qKey) : null;

                            return new SubmissionQuizQuestionViewDto(
                                    q.getQuestionId(),
                                    q.getText(),
                                    q.getOptions(),
                                    qKey != null ? studentAnswerMap.getOrDefault(qKey, "") : "",
                                    q.getCorrectAnswer(),
                                    grade != null ? grade.getAwardedPoints() : 0,
                                    grade != null ? grade.getMaxPoints() : q.getMarks(),
                                    grade != null ? grade.getFeedback() : ""
                            );
                        })
                        .toList();

        double totalAwardedPoints = merged.stream()
                .mapToDouble(SubmissionQuizQuestionViewDto::getAwardedMarks)
                .sum();

        double totalMaxPoints = merged.stream()
                .mapToDouble(SubmissionQuizQuestionViewDto::getMaxMarks)
                .sum();

        return new SubmissionViewDto(
                submission.getId(),
                submission.getStudentAdmissionId(),
                submission.getTargetId(),
                assessment.getTitle(),
                totalAwardedPoints,
                totalMaxPoints,
                merged
        );
    }


    private List<SubmissionAnswerDtos> parseSubmissionAnswer(String json) {
        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<SubmissionAnswerDtos>>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid submission JSON", e);
        }
    }
}



