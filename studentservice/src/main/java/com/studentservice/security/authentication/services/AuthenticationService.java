package com.studentservice.security.authentication.services;

import com.studentservice.security.configuration.JwtService;
import com.studentservice.security.dtos.AuthenticationRequest;
import com.studentservice.security.dtos.AuthenticationResponse;
import com.studentservice.security.dtos.RegisterRequest;
import com.studentservice.security.dtos.RegisterResponse;
import com.studentservice.student.entities.Role;
import com.studentservice.student.entities.Student;
import com.studentservice.student.exceptions.MissingFieldException;
import com.studentservice.student.exceptions.UserNotFoundException;
import com.studentservice.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final PasswordEncoder passwordEncoder;
    private final StudentRepository repository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        String email = request.getEmail();
        String password = request.getPassword().trim();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password

                )
        );
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        String role = user.getRole().toString();
        var jwtToken = jwtService.generateAuthenticationToken(user);



        return AuthenticationResponse.builder().
                token(jwtToken)
                .role(role)
                .build();
    }
    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword().trim();
        String confirmPassword = request.getConfirmPassword().trim();

        if (!password.equals(confirmPassword)) {
            throw new MissingFieldException("Passwords do not match!");
        }
        if (repository.findByEmail(email).isPresent()) {
            throw new MissingFieldException("Email already registered!");
        }
        Student newStudent = Student.builder()
                .email(email)
                .admissionYear(Long.parseLong(request.getAdmissionYear()))
                .gender(request.getGender())
                .admissionId(request.getAdmissionId())
                .className(request.getCourseName())
                .role(Role.STUDENT)
                .password(passwordEncoder.encode(password))
                .build();
        repository.save(newStudent);


        String jwtToken = jwtService.generateRegistrationToken(newStudent);

        return RegisterResponse.builder()
                .token(jwtToken)
                .build();
    }


}