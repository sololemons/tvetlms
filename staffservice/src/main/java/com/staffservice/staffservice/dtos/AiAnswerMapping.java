package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiAnswerMapping {
    private Map<String, String> studentAnswers;
    private Map<String, Long> questionKeyMap;
}
