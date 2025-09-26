package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    private String questionText;
    private String type;
    private int marks;
    private List<String>options;
    private String correctAnswer;
}
