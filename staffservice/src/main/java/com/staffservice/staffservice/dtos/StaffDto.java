package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffDto {
    private String firstName;
    private Long staffId;
    private String lastName;
    private String email;
    private String department;
    private String gender;
    private String phoneNumber;
    private long birthYear;
    private long admissionYear;
    private Role role;

}
