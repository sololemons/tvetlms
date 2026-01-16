package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatQuestionDto {
    private int questionId;
    private String text;
    private int marks;
    private Set<String> options;
}
