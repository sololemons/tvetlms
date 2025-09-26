package com.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffPayload {

        private String firstName;
        private String lastName;
        private String email;
        private String department;
        private String gender;
        private String phoneNumber;
        private long birthYear;
        private long admissionYear;
    }


