package com.studentservice.student.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledModuleDto {
    private Long id;
    private String moduleName;
    private String duration;
    private Long moduleId;
    private boolean isCompleted;
}
