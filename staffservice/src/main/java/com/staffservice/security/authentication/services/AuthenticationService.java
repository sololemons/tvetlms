package com.staffservice.security.authentication.services;
import com.staffservice.security.configuration.JwtService;
import com.staffservice.security.dtos.AuthenticationRequest;
import com.staffservice.security.dtos.AuthenticationResponse;
import com.staffservice.security.dtos.RegisterRequest;
import com.staffservice.security.dtos.RegisterResponse;
import com.staffservice.staffservice.entities.Staff;
import com.staffservice.staffservice.exceptions.MissingFieldException;
import com.staffservice.staffservice.exceptions.UserNotFoundException;
import com.staffservice.staffservice.repositories.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final PasswordEncoder passwordEncoder;
    private final StaffRepository repository;
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
        String role = user.getRole().toString();
        String emailAddress= user.getEmail();



        return AuthenticationResponse.builder().
                token(jwtToken)
                .role(role)
                .emailAddress(emailAddress)
                .build();
    }

    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword().trim();
        String confirmPassword = request.getConfirmPassword().trim();

        if (!password.equals(confirmPassword)) {
            throw new MissingFieldException("Passwords do not match!");
        }

        Staff existingStaff = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Email is not registered!"));

        if (existingStaff.getPassword() != null && !existingStaff.getPassword().isBlank()) {
            throw new MissingFieldException("Password is already set for this account.");
        }

        existingStaff.setPassword(passwordEncoder.encode(password));
        repository.save(existingStaff);

        String jwtToken = jwtService.generateRegistrationToken(existingStaff);

        return RegisterResponse.builder()
                .token(jwtToken)
                .build();
    }



}