package com.staffservice.staffservice.utillis;

import com.shared.dtos.AiGradeResponse;
import org.springframework.stereotype.Component;


@Component
public class AiGradeValidator {

    public void validate(AiGradeResponse response) {

        if (response == null) {
            throw new IllegalStateException("AI response is null");
        }

        requireNotNull(response.getSubmissionId(), "submissionId");
        requireNotNull(response.getStudentId(), "studentId");
        requireNotNull(response.getGradeLetter(), "gradeLetter");
        requireNotNull(response.getGradedAt(), "gradedAt");

        if (response.getTotalPoints() < 0 || response.getMaxPoints() <= 0) {
            throw new IllegalStateException("Invalid points in AI response");
        }

        if (response.getPercentage() < 0 || response.getPercentage() > 100) {
            throw new IllegalStateException("Invalid percentage in AI response");
        }

        if (response.getQuestionResults() == null || response.getQuestionResults().isEmpty()) {
            throw new IllegalStateException("Missing question results");
        }
    }

    private void requireNotNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException("Missing required field: " + field);
        }
    }
}


