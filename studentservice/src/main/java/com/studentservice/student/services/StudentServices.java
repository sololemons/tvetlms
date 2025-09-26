package com.studentservice.student.services;

import com.shared.dtos.SubmissionRequestDto;
import com.studentservice.student.configuration.RabbitMQConfiguration;
import com.studentservice.student.dtos.StudentDto;
import com.studentservice.student.entities.Student;
import com.studentservice.student.exceptions.UserNotFoundException;
import com.studentservice.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServices {

    private final StudentRepository studentRepository;
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
                student.getClassName()
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
}