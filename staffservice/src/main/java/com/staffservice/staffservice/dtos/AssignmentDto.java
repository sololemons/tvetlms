package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDto {
    private Long assignmentId;
    private String title;
    private LocalDateTime creationDate;
    private String description;
    private String dueDate;
    private List<String> classNames;
    private List<QuestionDto> questions;
}
