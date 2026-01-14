package com.staffservice.staffservice.utillis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.dtos.*;
import com.staffservice.security.configuration.AiGradingClient;
import com.staffservice.staffservice.configuration.RabbitMQConfiguration;
import com.staffservice.staffservice.dtos.AiAnswerMapping;
import com.staffservice.staffservice.dtos.SubmissionAnswerDto;
import com.staffservice.staffservice.entities.*;
import com.staffservice.staffservice.repositories.SubmissionRepository;
import com.staffservice.staffservice.retrofit.RetrofitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradingQuizSubmissionsService {

    private final SubmissionRepository submissionRepository;
    private final AiGradingClient aiGradingClient;
    private final RetrofitService retrofitService;
    private final ObjectMapper objectMapper;
    private final AiGradeValidator aiGradeValidator;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void gradeUngradedSubmissions() {
        LocalDateTime now = LocalDateTime.now();
        log.info("GamifyScheduler executed at {}", now);
        List<Submission> ungradedQuizSubmissions = submissionRepository
                .findBySubmissionStatusAndSubmissionType(SubmissionStatus.UNGRADED, SubmissionType.QUIZ);

        for (Submission submission : ungradedQuizSubmissions) {
            try {
                QuizAssessmentResponseDto quizAssessmentResponse = retrofitService
                        .getQuizAssessment(submission.getCourseId(), (int) submission.getTargetId(), submission.getModuleId());

                List<SubmissionAnswerDto> submissionAnswers = objectMapper.readValue(
                        submission.getSubmissionText(),
                        new TypeReference<List<SubmissionAnswerDto>>() {}
                );

                AiGradeRequest.QuizData quizData = mapToQuizData(quizAssessmentResponse,submission);

                AiAnswerMapping mapping = mapSubmissionToAiFormat(
                        submissionAnswers,
                        quizAssessmentResponse.getQuestions()
                );

                Map<String, String> studentAnswersMap = mapping.getStudentAnswers();

                submission.setQuestionKeyMapJson(
                        objectMapper.writeValueAsString(mapping.getQuestionKeyMap())
                );
                submissionRepository.save(submission);

                AiGradeRequest aiRequest = new AiGradeRequest();
                aiRequest.setSubmissionId(String.valueOf(submission.getSubmissionId()));
                aiRequest.setStudentId(submission.getStudentAdmissionId());
                aiRequest.setQuizId(String.valueOf(submission.getTargetId()));
                aiRequest.setTopic("AI");
                aiRequest.setQuizData(quizData);
                aiRequest.setStudentAnswers(studentAnswersMap);

                log.info("Sending grading request to AI for submission {}", submission.getSubmissionId());

                log.info("AI request {}" , aiRequest);
                AiGradeResponse aiResponse = aiGradingClient.gradeQuiz(aiRequest);
                log.info("AI Response {}", aiResponse);

                aiGradeValidator.validate(aiResponse);
                submission.setSubmissionStatus(SubmissionStatus.WAITING);
                submissionRepository.save(submission);


                GradeSubmissionEvent event = toEvent(aiResponse,submission);
                log.info("GradeSubmissionEvent {}", event);
                publishGradeEvent(event);

                log.info("Graded submission {} successfully", submission.getSubmissionId());

            } catch (Exception e) {
                log.error("Failed to grade submission {}: {}", submission.getSubmissionId(), e.getMessage(), e);
            }
        }
    }
    private AiGradeRequest.QuizData mapToQuizData(QuizAssessmentResponseDto quizAssessment, Submission submission) {
        AiGradeRequest.QuizData quizData = new AiGradeRequest.QuizData();
        quizData.setQuizId(String.valueOf(submission.getTargetId()));
        quizData.setGeneratedAt(LocalDateTime.now());
        quizData.setDifficultyLevel("intermediate");
        quizData.setTotalQuestions(quizAssessment.getQuestions().size());

        List<AiGradeRequest.MultipleChoiceQuestion> mcqs = new ArrayList<>();
        List<AiGradeRequest.TrueFalseQuestion> tfs = new ArrayList<>();
        List<AiGradeRequest.ShortAnswerQuestion> sas = new ArrayList<>();

        for (QuestionDto q : quizAssessment.getQuestions()) {
            String type = detectQuestionType(q);
            switch (type) {
                case "MCQ" -> {
                    AiGradeRequest.MultipleChoiceQuestion mcq = new AiGradeRequest.MultipleChoiceQuestion();
                    mcq.setQuestion(q.getText());

                    Map<String, String> optionMap = new LinkedHashMap<>();
                    char letter = 'A';
                    for (String optionText : q.getOptions()) {
                        optionMap.put(String.valueOf(letter++), optionText);
                    }
                    mcq.setOptions(optionMap);

                    mcq.setCorrectAnswer(q.getCorrectAnswer());
                    mcq.setExplanation("AI");
                    mcqs.add(mcq);
                }
                case "TRUE_FALSE" -> {
                    AiGradeRequest.TrueFalseQuestion tf = new AiGradeRequest.TrueFalseQuestion();
                    tf.setQuestion(q.getText());
                    tf.setCorrectAnswer(Boolean.parseBoolean(q.getCorrectAnswer()));
                    tf.setExplanation("AI");
                    tfs.add(tf);
                }
                case "SHORT_ANSWER" -> {
                    AiGradeRequest.ShortAnswerQuestion sa = new AiGradeRequest.ShortAnswerQuestion();
                    sa.setQuestion(q.getText());
                    sa.setKeyPoints(new ArrayList<>());
                    sa.setSampleAnswer("AI");
                    sas.add(sa);
                }
            }
        }

        quizData.setMultipleChoice(mcqs);
        quizData.setTrueFalse(tfs);
        quizData.setShortAnswer(sas);

        return quizData;
    }

    public String detectQuestionType(QuestionDto q) {

        if (q.getOptions() != null
                && q.getOptions().size() == 2
                && q.getOptions().stream().allMatch(
                opt -> opt.equalsIgnoreCase("true") || opt.equalsIgnoreCase("false"))
                && (q.getCorrectAnswer().equalsIgnoreCase("true")
                || q.getCorrectAnswer().equalsIgnoreCase("false"))) {

            return "TRUE_FALSE";
        }

        if (q.getOptions() != null && q.getOptions().size() > 2) {
            return "MCQ";
        }

        return "SHORT_ANSWER";
    }

    private AiAnswerMapping mapSubmissionToAiFormat(
            List<SubmissionAnswerDto> submissionAnswers,
            List<QuestionDto> quizQuestions
    ) {
        Map<Long, QuestionDto> questionMap = quizQuestions.stream()
                .collect(Collectors.toMap(QuestionDto::getQuestionId, q -> q));

        Map<String, String> aiAnswers = new LinkedHashMap<>();
        Map<String, Long> questionKeyMap = new LinkedHashMap<>();

        int mcqCounter = 0, tfCounter = 0, saCounter = 0;

        for (SubmissionAnswerDto ans : submissionAnswers) {
            QuestionDto question = questionMap.get(ans.getQuestionId());
            if (question == null) continue;

            String key;

            switch (detectQuestionType(question)) {
                case "MCQ" -> key = "mcq_" + mcqCounter++;
                case "TRUE_FALSE" -> key = "tf_" + tfCounter++;
                default -> key = "sa_" + saCounter++;
            }

            aiAnswers.put(key, ans.getAnswerText());
            questionKeyMap.put(key, question.getQuestionId());
        }

        return new AiAnswerMapping(aiAnswers, questionKeyMap);
    }

    public GradeSubmissionEvent toEvent(AiGradeResponse aiResponse, Submission submission) {

    AssessmentDetails assessmentDetails = new AssessmentDetails();
    assessmentDetails.setAssessmentType(String.valueOf(submission.getSubmissionType()));
    assessmentDetails.setClassName(submission.getClassName());
    assessmentDetails.setCourseId(submission.getCourseId());
    assessmentDetails.setModuleId(submission.getModuleId());
    assessmentDetails.setTargetId((int) submission.getTargetId());

    GradeSubmissionEvent gradeSubmissionEvent =new GradeSubmissionEvent();
    gradeSubmissionEvent.setSubmissionId(String.valueOf(submission.getSubmissionId()));
    gradeSubmissionEvent.setGradedAt(aiResponse.getGradedAt());
    gradeSubmissionEvent.setMaxPoints(aiResponse.getMaxPoints());
    gradeSubmissionEvent.setAssessmentDetails(assessmentDetails);
    gradeSubmissionEvent.setOverallFeedback(aiResponse.getOverallFeedback());
    gradeSubmissionEvent.setPercentage(aiResponse.getPercentage());
    gradeSubmissionEvent.setQuestionResults(aiResponse.getQuestionResults());
    gradeSubmissionEvent.setStudentAdmissionId(aiResponse.getStudentId());
    gradeSubmissionEvent.setTotalPoints(aiResponse.getTotalPoints());
    gradeSubmissionEvent.setTopicMastery(aiResponse.getTopicMastery());
    gradeSubmissionEvent.setClassName(submission.getClassName());
    gradeSubmissionEvent.setQuestionKeyMapJson(submission.getQuestionKeyMapJson());
    return gradeSubmissionEvent;
    }
    public void publishGradeEvent(GradeSubmissionEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.GRADE_SUBMISSION_QUEUE,
                event
        );
    }




}
