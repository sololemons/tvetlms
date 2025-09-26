package com.studentservice.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String admissionId;
    private String admissionYear;
    private String gender;
    private String courseName;
    private String password;
    private String confirmPassword;
}
