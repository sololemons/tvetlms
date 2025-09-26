package com.studentservice.student.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private String email;
    private String admissionId;
    private long admissionYear;
    private String gender;
    private String className;

}
