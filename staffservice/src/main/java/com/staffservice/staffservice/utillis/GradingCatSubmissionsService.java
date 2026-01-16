package com.staffservice.staffservice.utillis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.dtos.*;
import com.staffservice.security.configuration.AiGradingClient;
import com.staffservice.staffservice.dtos.AiAnswerMapping;
import com.staffservice.staffservice.dtos.SubmissionAnswerDto;
import com.staffservice.staffservice.entities.Submission;
import com.staffservice.staffservice.entities.SubmissionStatus;
import com.staffservice.staffservice.repositories.SubmissionRepository;
import com.staffservice.staffservice.retrofit.RetrofitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GradingCatSubmissionsService {
    private final SubmissionRepository submissionRepository;
    private final RetrofitService retrofitService;
    private final ObjectMapper objectMapper;
    private final GradingQuizSubmissionsService gradingQuizSubmissionsService;
    private final AiGradeValidator aiGradeValidator;
    private final AiGradingClient aiGradingClient;

    @Transactional
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void gradeUngradedCatSubmissions() {

        log.info("CAT Grading Scheduler started");

        List<Submission> submissions = submissionRepository
                .findBySubmissionStatusAndSubmissionType(
                        SubmissionStatus.UNGRADED,
                        SubmissionType.CAT
                );

        for (Submission submission : submissions) {
            try {
                CatAssessmentResponseDto catAssessment = retrofitService
                        .getCatAssessment(
                                submission.getCourseId(),
                                (int) submission.getTargetId()
                        );

                List<SubmissionAnswerDto> answers = objectMapper.readValue(
                        submission.getSubmissionText(),
                        new TypeReference<>() {
                        }
                );

                AiCatGradeRequest.CatData catData = mapToCatData(catAssessment);

                AiAnswerMapping mapping = mapCatSubmissionToAiFormat(
                        answers,
                        catAssessment.getQuestions()
                );

                Map<String, String> studentAnswers = mapping.getStudentAnswers();

                submission.setQuestionKeyMapJson(
                        objectMapper.writeValueAsString(mapping.getQuestionKeyMap())
                );
                submissionRepository.save(submission);


                AiCatGradeRequest request = new AiCatGradeRequest();
                request.setSubmissionId(String.valueOf(submission.getId()));
                request.setStudentId(submission.getStudentAdmissionId());
                request.setCatId(String.valueOf(submission.getTargetId()));
                request.setTopic("CAT");
                request.setCatData(catData);
                request.setStudentAnswers(studentAnswers);

                log.info("Sending CAT grading request for submission {}", submission.getId());

                AiGradeResponse response = aiGradingClient.gradeCat(request);

                log.info("AI CAT Response: {}", response);

                aiGradeValidator.validate(response);

                GradeSubmissionEvent event = gradingQuizSubmissionsService.toEvent(response, submission);
                gradingQuizSubmissionsService.publishGradeEvent(event);

                log.info("CAT submission {} graded successfully", submission.getId());

            } catch (Exception e) {
                log.error("CAT grading failed for submission {}",
                        submission.getId(), e);
            }
        }
    }

    private AiCatGradeRequest.CatData mapToCatData(CatAssessmentResponseDto catAssessmentResponseDto) {
        AiCatGradeRequest.CatData catData = new AiCatGradeRequest.CatData();
        catData.setCatId("1");
        catData.setGeneratedAt(LocalDateTime.now());
        catData.setDifficultyLevel("intermediate");
        catData.setTotalQuestions(catAssessmentResponseDto.getQuestions().size());

        List<AiCatGradeRequest.MultipleChoiceQuestion> mcqs = new ArrayList<>();
        List<AiCatGradeRequest.TrueFalseQuestion> tfs = new ArrayList<>();
        List<AiCatGradeRequest.ShortAnswerQuestion> sas = new ArrayList<>();

        for (QuestionDto q : catAssessmentResponseDto.getQuestions()) {
            String type = gradingQuizSubmissionsService.detectQuestionType(q);
            switch (type) {
                case "MCQ" -> {
                    AiCatGradeRequest.MultipleChoiceQuestion mcq = new AiCatGradeRequest.MultipleChoiceQuestion();
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
                    AiCatGradeRequest.TrueFalseQuestion tf = new AiCatGradeRequest.TrueFalseQuestion();
                    tf.setQuestion(q.getText());
                    tf.setCorrectAnswer(Boolean.parseBoolean(q.getCorrectAnswer()));
                    tf.setExplanation("AI");
                    tfs.add(tf);
                }
                case "SHORT_ANSWER" -> {
                    AiCatGradeRequest.ShortAnswerQuestion sa = new AiCatGradeRequest.ShortAnswerQuestion();
                    sa.setQuestion(q.getText());
                    sa.setKeyPoints(new ArrayList<>());
                    sa.setSampleAnswer("AI");
                    sas.add(sa);
                }
            }
        }

        catData.setMultipleChoice(mcqs);
        catData.setTrueFalse(tfs);
        catData.setShortAnswer(sas);

        return catData;
    }
    private AiAnswerMapping mapCatSubmissionToAiFormat(
            List<SubmissionAnswerDto> submissionAnswers,
            List<QuestionDto> catQuestions
    ) {
        Map<Integer, QuestionDto> questionMap = catQuestions.stream()
                .collect(Collectors.toMap(QuestionDto::getQuestionId, q -> q));

        Map<String, String> aiAnswers = new LinkedHashMap<>();
        Map<String, Long> questionKeyMap = new LinkedHashMap<>();

        int mcqCounter = 0, tfCounter = 0, saCounter = 0;

        for (SubmissionAnswerDto ans : submissionAnswers) {
            QuestionDto question = questionMap.get(ans.getQuestionId());
            if (question == null) continue;

            String key;

            switch (gradingQuizSubmissionsService.detectQuestionType(question)) {
                case "MCQ" -> key = "mcq_" + mcqCounter++;
                case "TRUE_FALSE" -> key = "tf_" + tfCounter++;
                default -> key = "sa_" + saCounter++;
            }

            aiAnswers.put(key, ans.getAnswerText());
            questionKeyMap.put(key, Long.valueOf(question.getQuestionId()));
        }

        return new AiAnswerMapping(aiAnswers, questionKeyMap);
    }

}