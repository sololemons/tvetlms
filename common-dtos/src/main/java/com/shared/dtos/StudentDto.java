package com.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {

    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String stream;
    private int className;
    private long admissionYear;
}
