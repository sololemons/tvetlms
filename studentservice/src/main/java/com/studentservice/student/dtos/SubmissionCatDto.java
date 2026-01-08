package com.studentservice.student.dtos;

import com.shared.dtos.AnswerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionCatDto {
    private int courseId;
    private int catId;
    private List<AnswerDto> answers;
}
