package com.coursemanagement.dtos;

import lombok.Data;

@Data
public class TrueFalseQuestion {

    private String questionText;
    private boolean correctAnswer;
    private String explanation;
    private int points;
    private String difficulty;
    private String subtopic;
}
