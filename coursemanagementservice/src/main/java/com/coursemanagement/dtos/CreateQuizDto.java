package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuizDto {
    private String quizTitle;
    private String quizDescription;
    private String dueDate;
    private int courseId;
    private int moduleId;
}
