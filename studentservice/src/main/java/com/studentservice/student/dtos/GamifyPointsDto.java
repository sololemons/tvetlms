package com.studentservice.student.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamifyPointsDto {
    private long points;
    private String admissionId;
    private String studentName;
}
