package com.studentservice.student.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDto {
    private String email;
    private long admissionId;
    private long admissionYear;
    private String gender;
}
