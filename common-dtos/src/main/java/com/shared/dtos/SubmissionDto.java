package com.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionDto {
        private long submissionId;
        private String studentAdmissionId;
        private long assignmentId;
        private String className;
        private LocalDateTime submissionDate;
        private String submissionStatus;
        private boolean submitted;
        private List<AnswerDto> answers;
}
