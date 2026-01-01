package com.coursemanagement.dtos;

import lombok.Data;

@Data
public class OpenEndedQuestion {

    private String questionText;
    private String expectedAnswer;
    private int points;
    private String difficulty;
    private String subtopic;
}

