package com.studentservice.student.services;

import com.shared.dtos.CertificateRequestDto;
import com.shared.dtos.SubmissionRequestDto;
import com.studentservice.student.configuration.RabbitMQConfiguration;
import com.studentservice.student.configuration.retrofit.RetrofitService;
import com.studentservice.student.dtos.*;
import com.shared.dtos.ModuleDto;
import com.studentservice.student.entities.EnrolledCourses;
import com.studentservice.student.entities.EnrolledModules;
import com.studentservice.student.entities.Student;
import com.studentservice.student.exceptions.UserNotFoundException;
import com.studentservice.student.repository.EnrolledModulesRepository;
import com.studentservice.student.repository.EnrolledCourseRepository;
import com.studentservice.student.repository.StudentRepository;
import com.studentservice.student.utilis.EnrolledCourseMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServices {

    private final StudentRepository studentRepository;
    private final EnrolledModulesRepository enrolledModulesRepository;
    private final EnrolledCourseRepository enrolledRepository;
    private final RetrofitService retrofitService;
    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(StudentServices.class);

    public Page<StudentDto> getFilteredStudents(Long admissionId, Integer admissionYear, Pageable pageable) {
        Page<Student> students = studentRepository.findByFilters(admissionId, admissionYear, pageable);
        return students.map(this::mapToDto);
    }


    public List<StudentDto> getAllStudents() {
        List<Student> students = studentRepository.findAll();

        return students.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());


    }

    private StudentDto mapToDto(Student student) {
        return new StudentDto(
                student.getEmail(),
                student.getAdmissionId(),
                student.getAdmissionYear(),
                student.getGender(),
                student.getClassName(),
                student.getFirstName(),
                student.getLastName()
        );
    }

    public StudentDto getStudentByAdmissionId(Long admissionId) {
        Optional<Student> studentOptional = studentRepository.findById(admissionId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            return mapToDto(student);
        } else {
            throw new UserNotFoundException("Student not found with admission ID: " + admissionId);
        }
    }

    public String submitAssignment(SubmissionRequestDto submissionRequestDto) {
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.ADD_ASSIGNMENT_QUEUE, submissionRequestDto);
        log.info("SubmissionRequestDto: {} ", submissionRequestDto);
        return "Assignment submitted";
    }


    public StudentDto updateStudent(StudentDto studentDto) {
        String admissionId = studentDto.getAdmissionId();
        Optional<Student> existingStudent = studentRepository.findById(Long.valueOf(admissionId));

        if (existingStudent.isPresent()) {

            Student studentToUpdate = existingStudent.get();

            studentToUpdate.setGender(studentDto.getGender());
            studentToUpdate.setAdmissionYear(studentDto.getAdmissionYear());
            studentToUpdate.setEmail(studentDto.getEmail());

            Student updatedStudent = studentRepository.save(studentToUpdate);


            return studentDto;
        } else {
            throw new UserNotFoundException("Failed to Update student with admission ID: " + admissionId);
        }
    }

    public String deleteStudent(Long id) {
        Optional<Student> existingStudent = studentRepository.findById(id);
        if (existingStudent.isPresent()) {
            studentRepository.delete(existingStudent.get());
        } else {
            throw new UserNotFoundException("Student not found with id: " + id);
        }
        return "Student deleted successfully";
    }


    public StudentDto getActiveStudent(Principal principal) {
        String email = principal.getName();
        Optional<Student> studentOptional = studentRepository.findByEmail(email);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            return mapToDto(student);
        }
        throw new UserNotFoundException("Student not found with email: " + email);
    }
    @Transactional
    public String enrollCourses(EnrollDto enrollDto) {
        Student student = studentRepository.findByAdmissionId(enrollDto.getAdmissionId())
                .orElseThrow(() -> new UserNotFoundException("Student not found"));

        long courseId = Long.parseLong(enrollDto.getCourseId());

        boolean alreadyEnrolled = enrolledRepository.existsByStudent_AdmissionIdAndCourseId(enrollDto.getAdmissionId(), courseId);
        if (alreadyEnrolled) {
            return " Student is already enrolled in course: " + enrollDto.getCourseName();
        }

        EnrolledCourses enrolledCourse = new EnrolledCourses();
        enrolledCourse.setCourseId(courseId);
        enrolledCourse.setCourseName(enrollDto.getCourseName());
        enrolledCourse.setProgression("0%");
        enrolledCourse.setCompleted(false);
        enrolledCourse.setStudent(student);
        enrolledRepository.save(enrolledCourse);

        student.getEnrolledCourses().add(enrolledCourse);
        studentRepository.save(student);

        List<ModuleDto> modules = retrofitService.getModules((int) courseId);

        for (ModuleDto module : modules) {
            EnrolledModules enrolledModule = new EnrolledModules();
            enrolledModule.setCompleted(false);
            enrolledModule.setEnrolledCourse(enrolledCourse);
            enrolledModule.setModuleName(module.getModuleName());
            enrolledModule.setDuration(module.getWeek());
            enrolledModule.setModuleId(module.getModuleId());
            enrolledModulesRepository.save(enrolledModule);
        }

        return "✅ Student successfully enrolled in course: " + enrollDto.getCourseName();
    }


    @Transactional
    public String setModuleDone(MarkModuleDoneDto markModuleDoneDto, Principal principal) {
        String userName = principal.getName();
        Student student = studentRepository.findByEmail(userName).orElseThrow(() -> new UserNotFoundException("Student not found"));
        String admissionId = student.getAdmissionId();
        EnrolledModules enrolledModule = enrolledModulesRepository.findByModuleIdAndEnrolledCourse_CourseIdAndEnrolledCourse_Student_AdmissionId(markModuleDoneDto.getModuleId(), markModuleDoneDto.getCourseId(), admissionId);
        if (enrolledModule == null) {
            throw new IllegalArgumentException("Module not found: " + markModuleDoneDto.getModuleId() +"in Course with Course Id" + markModuleDoneDto.getCourseId());
        }

        enrolledModule.setCompleted(true);
        enrolledModulesRepository.save(enrolledModule);

        EnrolledCourses enrolledCourse = enrolledModule.getEnrolledCourse();

        StudentDto studentDto = mapToDto(student);

        updateCourseProgression(enrolledCourse, studentDto);

        return "Module Completed: " + enrolledModule.getModuleName() +
                " | Current Progression: " + enrolledCourse.getProgression();
    }
    private void updateCourseProgression(EnrolledCourses enrolledCourse, StudentDto studentDto) {
        List<EnrolledModules> modules = enrolledModulesRepository.findByEnrolledCourse_CourseId(enrolledCourse.getCourseId());

        long total = modules.size();
        long completed = modules.stream().filter(EnrolledModules::isCompleted).count();

        String progression = (total == 0) ? "0%" : (completed * 100 / total) + "%";

        enrolledCourse.setProgression(progression);
        enrolledCourse.setCompleted(completed == total);
        enrolledRepository.save(enrolledCourse);

        if (enrolledCourse.isCompleted()) {
            CertificateRequestDto certificateRequest = new CertificateRequestDto();
            certificateRequest.setCourseId(enrolledCourse.getCourseId());
            certificateRequest.setCourseName(enrolledCourse.getCourseName());
            certificateRequest.setStudentId(studentDto.getAdmissionId());
            certificateRequest.setEmail(studentDto.getEmail());
            certificateRequest.setStudentFirstName(studentDto.getFirstName());
            certificateRequest.setStudentLastName(studentDto.getLastName());
            certificateRequest.setCompletionDate(LocalDate.now());
            rabbitTemplate.convertAndSend(RabbitMQConfiguration.GENERATE_CERTIFICATE_QUEUE, certificateRequest);
        }
    }

    public String completeProfile(ProfileDto profileDto,Principal principal) {
        String userName = principal.getName();
        Student student = studentRepository.findByEmail(userName).orElseThrow(() -> new UserNotFoundException("Student not found with email: " + userName));
        student.setFirstName(profileDto.getFirstName());
        student.setLastName(profileDto.getLastName());
        studentRepository.save(student);
        return "Profile Completed and Updated";

    }
   @Transactional
    public List<EnrolledCoursesDto> fetchAllEnrolledCourses(Principal principal) {
    String userName = principal.getName();
    Student student = studentRepository.findByEmail(userName).orElseThrow(() -> new UserNotFoundException("Student not found with email: " + userName));
            return enrolledRepository.findByStudent_AdmissionId(student.getAdmissionId())
                    .stream()
                    .map(EnrolledCourseMapper::toDto)
                    .collect(Collectors.toList());
        }
    }
