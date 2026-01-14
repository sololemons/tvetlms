package com.staffservice.staffservice.utillis;

import com.shared.dtos.MarkSubmissionGradedDto;
import com.staffservice.staffservice.configuration.RabbitMQConfiguration;
import com.staffservice.staffservice.entities.SubmissionStatus;
import com.staffservice.staffservice.repositories.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarkSubmissionService {
    private final SubmissionRepository submissionRepository;


    @Transactional
    @RabbitListener(queues = RabbitMQConfiguration.MARK_SUBMISSION_GRADED_QUEUE)
    public void markSubmissionGraded(MarkSubmissionGradedDto dto) {
        try {
            Long submissionId = (long) dto.getSubmissionId();
            log.info("Marking submission {} as graded", submissionId);

            submissionRepository.findById(submissionId).ifPresentOrElse(submission -> {
                submission.setSubmissionStatus(SubmissionStatus.GRADED);
                submissionRepository.save(submission);
                log.info("Submission {} marked as GRADED", submissionId);
            }, () -> log.warn("Submission {} not found, skipping", submissionId));

        } catch (Exception e) {
            log.error("Failed to mark submission {} as graded: {}", dto.getSubmissionId(), e.getMessage(), e);
        }
    }

}
