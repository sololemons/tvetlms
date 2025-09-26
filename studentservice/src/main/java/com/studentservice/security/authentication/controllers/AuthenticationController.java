package com.studentservice.security.authentication.controllers;
import com.studentservice.security.authentication.services.AuthenticationService;
import com.studentservice.security.dtos.AuthenticationRequest;
import com.studentservice.security.dtos.AuthenticationResponse;
import com.studentservice.security.dtos.RegisterRequest;
import com.studentservice.security.dtos.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/auth/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

}