package com.adminservice.security.authentication.services;

import com.adminservice.adminservice.dtos.Role;
import com.adminservice.adminservice.entities.Admin;
import com.adminservice.adminservice.exceptions.UserNotFoundException;
import com.adminservice.adminservice.repositories.AdminRepository;
import com.adminservice.security.configuration.JwtService;
import com.adminservice.security.dtos.AuthenticationRequest;
import com.adminservice.security.dtos.AuthenticationResponse;
import com.adminservice.security.dtos.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final PasswordEncoder passwordEncoder;
    private final AdminRepository repository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        // Validate phone number before authentication
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
        var jwtToken = jwtService.generateAuthenticationToken(user);
        var emailAddress = user.getEmail();



        return AuthenticationResponse.builder().
                token(jwtToken)
                .role(String.valueOf(Role.ADMIN))
                .emailAddress(emailAddress)
                .build();

    }

    public AuthenticationResponse register(RegisterRequest request) {

       String email = request.getEmail();
        String password = request.getPassword().trim();
        String confirmPassword = request.getConfirmPassword().trim();


        if (!password.equals(confirmPassword)) {
            throw new UserNotFoundException("Passwords do not match!");
        }
        if (repository.findByEmail(email).isPresent()) {
            throw new UserNotFoundException("Email is already registered!");
        }
        var admin = Admin.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(email)
                .role(Role.ADMIN)
                .password(passwordEncoder.encode(password))
                .build();
        repository.save(admin);
        var jwtToken = jwtService.generateRegistrationToken(admin);

        return AuthenticationResponse.builder().
                token(jwtToken)
                .build();

    }


}