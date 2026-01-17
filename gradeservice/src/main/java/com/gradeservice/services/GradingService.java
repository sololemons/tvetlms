package com.gradeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradeservice.configuration.RabbitMQConfiguration;
import com.gradeservice.entities.QuestionGrade;
import com.gradeservice.entities.SubmissionGrade;
import com.gradeservice.repositories.GradeRepository;
import com.shared.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradingService {



        private final GradeRepository gradeRepository;
        private final RabbitTemplate rabbitTemplate;

        @Transactional
        @RabbitListener(queues = RabbitMQConfiguration.GRADE_SUBMISSION_QUEUE)
        public void saveAiGrade(GradeSubmissionEvent aiResponse) {

           log.info("SubmissionsEvent {}", aiResponse);
            SubmissionGrade grade = new SubmissionGrade();
            grade.setSubmissionId(aiResponse.getSubmissionId());
            grade.setCourseId(String.valueOf(aiResponse.getAssessmentDetails().getCourseId()));
            grade.setModuleId(String.valueOf(aiResponse.getAssessmentDetails().getModuleId()));
            grade.setStudentAdmissionId(aiResponse.getStudentAdmissionId());
            grade.setTargetId(String.valueOf(aiResponse.getAssessmentDetails().getTargetId()));
            grade.setSubmissionType(aiResponse.getAssessmentDetails().getAssessmentType());
            grade.setTotalPoints(aiResponse.getTotalPoints());
            grade.setMaxPoints(aiResponse.getMaxPoints());
            grade.setPercentage(aiResponse.getPercentage());
            grade.setGradedAt(aiResponse.getGradedAt());


            List<QuestionGrade> questionGrades = aiResponse.getQuestionResults()
                    .stream()
                    .map(q -> mapQuestionGrade(q, grade))
                    .toList();

            grade.setQuestionGrades(questionGrades);

            gradeRepository.save(grade);
            MarkSubmissionGradedDto markSubmissionGradedDto = new MarkSubmissionGradedDto();
            markSubmissionGradedDto.setSubmissionId(Integer.parseInt(aiResponse.getSubmissionId()));
             rabbitTemplate.convertAndSend(RabbitMQConfiguration.MARK_SUBMISSION_GRADED_QUEUE, markSubmissionGradedDto);

        }

        private QuestionGrade mapQuestionGrade(AiGradeResponse.QuestionResult q, SubmissionGrade grade) {
            QuestionGrade g = new QuestionGrade();
            g.setQuestionId(q.getQuestionId());
            g.setQuestionType(q.getQuestionType());
            g.setMaxPoints(q.getMaxPoints());
            g.setAwardedPoints(q.getAwardedPoints());
            g.setIsCorrect(q.getIsCorrect());
            g.setFeedback(q.getFeedback());
            g.setStrengths(q.getStrengths());
            g.setImprovements(q.getImprovements());
            g.setSubmissionGrade(grade);
            return g;
        }
    @Transactional
    @RabbitListener(queues = RabbitMQConfiguration.ASSIGNMENT_GRADING_QUEUE)
    public void handleAssignmentGrading(AssignmentGradeResponseDto dto) {

        log.info("Received assignment grading event: {}", dto);

        double percentage =
                ((double) dto.getAwardedMarks() / dto.getMaxMarks()) * 100;

        SubmissionGrade grade = new SubmissionGrade();
        grade.setGradedAt(LocalDateTime.parse(dto.getGradedAt()));
        grade.setCourseId(String.valueOf(dto.getAssessmentDetails().getCourseId()));
        grade.setTargetId(String.valueOf(dto.getAssessmentDetails().getTargetId()));
        grade.setClassName(dto.getAssessmentDetails().getClassName());
        grade.setSubmissionId(String.valueOf(dto.getSubmissionId()));
        grade.setStudentAdmissionId(dto.getAdmissionId());
        grade.setSubmissionType(String.valueOf(SubmissionType.ASSIGNMENT));
        grade.setTotalPoints(dto.getAwardedMarks());
        grade.setMaxPoints(dto.getMaxMarks());
        grade.setPercentage(percentage);
        grade.setGradedAt(LocalDateTime.now());

        gradeRepository.save(grade);
        MarkSubmissionGradedDto markSubmissionGradedDto = new MarkSubmissionGradedDto();
        markSubmissionGradedDto.setSubmissionId(Integer.parseInt(String.valueOf(dto.getSubmissionId())));
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.MARK_SUBMISSION_GRADED_QUEUE, markSubmissionGradedDto);

        log.info("Assignment submission {} graded successfully", dto.getSubmissionId());
    }

    }


