package com.adminservice.adminservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDto {
    private String password;
    private String firstName;
    private String lastName;
    private String email;


}
