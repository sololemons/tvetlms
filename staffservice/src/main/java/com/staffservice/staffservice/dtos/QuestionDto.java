package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    private Long questionId;
    private String text;
    private String type;
   private Set<String> options;
}
