package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiQuizRequest {

    private String content;
    private String difficulty_level;

    private int num_mcq;
    private int num_true_false;
    private int num_short_answer;

    private int num_of_options;
}
