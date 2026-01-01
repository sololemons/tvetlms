package com.coursemanagement.dtos;

import lombok.Data;

import java.util.List;

@Data
public class McqQuestion {

    private String questionText;
    private List<OptionDto> options;
    private String correctAnswer;
    private String explanation;
    private int points;
    private String difficulty;
    private String subtopic;
}
