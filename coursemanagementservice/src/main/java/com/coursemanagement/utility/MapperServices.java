package com.coursemanagement.utility;

import com.coursemanagement.dtos.*;
import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.CourseModule;
import com.coursemanagement.entity.QuizAssessment;
import com.coursemanagement.entity.QuizQuestions;
import com.coursemanagement.repository.CourseModuleRepository;
import com.shared.dtos.ModuleDto;
import com.shared.dtos.QuestionDto;
import com.shared.dtos.QuizAssessmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapperServices {
    private CourseModuleRepository courseModuleRepository;
    @Transactional
    public void mapAndSaveQuiz(GroqQuizResponse aiResponse, CourseModule module) {

        QuizAssessment quiz = new QuizAssessment();
        quiz.setModule(module);

        Set<QuizQuestions> questions = new HashSet<>();

        if (aiResponse.getMcqQuestions() != null) {
            questions.addAll(aiResponse.getMcqQuestions().stream().map(q -> {
                QuizQuestions qq = new QuizQuestions();
                qq.setQuestionText(q.getQuestionText());
                qq.setMarks(q.getPoints());
                qq.setCorrectAnswer(q.getCorrectAnswer());
                qq.setOptions(q.getOptions() == null ? new HashSet<>() :
                        q.getOptions().stream().map(OptionDto::getText).collect(Collectors.toSet()));
                qq.setQuizAssessment(quiz);
                return qq;
            }).collect(Collectors.toSet()));
        }

        if (aiResponse.getTrueFalseQuestions() != null) {
            questions.addAll(aiResponse.getTrueFalseQuestions().stream().map(q -> {
                QuizQuestions qq = new QuizQuestions();
                qq.setQuestionText(q.getQuestionText());
                qq.setMarks(q.getPoints());
                // qq.setCorrectAnswer(q.getCorrectAnswer().toString());
                qq.setOptions(Set.of("True", "False"));
                qq.setQuizAssessment(quiz);
                return qq;
            }).collect(Collectors.toSet()));
        }

        // Map Open-ended questions
        if (aiResponse.getOpenEndedQuestions() != null) {
            questions.addAll(aiResponse.getOpenEndedQuestions().stream().map(q -> {
                QuizQuestions qq = new QuizQuestions();
                qq.setQuestionText(q.getQuestionText());
                qq.setMarks(q.getPoints());
                // qq.setCorrectAnswer(q.getCorrectAnswer());
                qq.setOptions(new HashSet<>());
                qq.setQuizAssessment(quiz);
                return qq;
            }).collect(Collectors.toSet()));
        }

        quiz.setQuizQuestions(questions);

        module.getQuizAssessments().add(quiz);
        courseModuleRepository.save(module);


    }
    public CourseDto mapToDto(Course course) {

        CourseDto courseDto = new CourseDto();
        courseDto.setCourseName(course.getCourseName());
        courseDto.setDescription(course.getDescription());
        courseDto.setCourseOverview(course.getCourseOverview());


        if (course.getCats() != null) {
            List<CatAssessmentDto> catAssessmentDto = course.getCats().
                    stream().
                    map
                            (cat -> {
                                List<CatQuestionDto> questionDto = cat.getCatQuestions() == null ? List.of() : cat.getCatQuestions().stream().
                                        map(q -> new CatQuestionDto(
                                                q.getQuestionId(),
                                                q.getQuestionText(),
                                                q.getMarks(),
                                                q.getOptions()

                                        )).toList();

                                CatAssessmentDto dto = new CatAssessmentDto();
                                dto.setTitle(cat.getTitle());
                                dto.setDurationMinutes(cat.getDurationMinutes());
                                dto.setStartTime(cat.getStartTime());
                                dto.setEndTime(cat.getEndTime());
                                dto.setQuestions(questionDto);

                                return dto;
                            }).toList();

            courseDto.setCatAssessmentDto(catAssessmentDto);
        }

        if (course.getModules() != null) {
            List<ModuleDto> moduleDto = course.getModules().
                    stream().
                    map(module ->
                    {
                        List<QuizAssessmentDto> quizDto =
                                module.getQuizAssessments() == null ? List.of() : module.getQuizAssessments().
                                        stream().
                                        map(quiz ->
                                        {
                                            List<QuestionDto> questionDto = quiz.getQuizQuestions() == null ? List.of() : quiz.getQuizQuestions()
                                                    .stream().
                                                    map(q -> new QuestionDto(
                                                            q.getQuestionId(),
                                                            q.getQuestionText(),
                                                            q.getMarks(),
                                                            q.getOptions()
                                                    )).toList();
                                            return new QuizAssessmentDto(
                                                    quiz.getTitle(),
                                                    questionDto);
                                        })
                                        .toList();

                        return new ModuleDto(

                                module.getModuleName(),
                                module.getWeek(),
                                module.getContent(),
                                module.getModuleId(),
                                quizDto);
                    }).toList();

            courseDto.setModuleDto(moduleDto);
        }

        return courseDto;
    }
    public ModuleDto mapToDtoModule(CourseModule courseModule) {

        List<QuizAssessmentDto> quizDto = courseModule.getQuizAssessments().stream().map(quiz -> {
            List<QuestionDto> questionDto = quiz.getQuizQuestions() == null

                    ? List.of() : quiz.getQuizQuestions().stream().map(q -> new QuestionDto(q.getQuestionId(), q.getQuestionText(), q.getMarks(), q.getOptions())).toList();


            return new QuizAssessmentDto(quiz.getTitle(), questionDto);
        }).toList();

        return new ModuleDto(courseModule.getWeek(), courseModule.getModuleName(), courseModule.getContent(), courseModule.getModuleId(), quizDto);
    }

}
