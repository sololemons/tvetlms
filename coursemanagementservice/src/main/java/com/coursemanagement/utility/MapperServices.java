package com.coursemanagement.utility;

import com.coursemanagement.dtos.*;
import com.coursemanagement.entity.*;
import com.coursemanagement.repository.CourseModuleRepository;
import com.coursemanagement.repository.CourseRepository;
import com.shared.dtos.ModuleDto;
import com.shared.dtos.QuestionDto;
import com.shared.dtos.QuizAssessmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapperServices {
    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;

    @Transactional
    public void mapAndSaveQuiz(GroqQuizResponse aiResponse, CourseModule module, QuizAssessment quizAssessment) {
        log.info("AI response {}", aiResponse);

        quizAssessment.setModule(module);

        if (quizAssessment.getQuizQuestions() == null) {
            quizAssessment.setQuizQuestions(new HashSet<>());
        }

        quizAssessment.getQuizQuestions().clear();

        if (aiResponse.getMultiple_choice() != null) {
            aiResponse.getMultiple_choice().forEach(q -> {
                QuizQuestions qq = new QuizQuestions();
                qq.setQuestionText(q.getQuestion());
                qq.setMarks(1);
                qq.setCorrectAnswer(q.getCorrect_answer());
                qq.setOptions(q.getOptions() == null ? new HashSet<>() :
                        new HashSet<>(q.getOptions().values()));
                qq.setQuizAssessment(quizAssessment);
                quizAssessment.getQuizQuestions().add(qq);
            });
        }

        if (aiResponse.getTrue_false() != null) {
            aiResponse.getTrue_false().forEach(q -> {
                QuizQuestions qq = new QuizQuestions();
                qq.setQuestionText(q.getQuestion());
                qq.setMarks(1);
                qq.setCorrectAnswer(Boolean.toString(q.isCorrect_answer()));
                qq.setOptions(Set.of("True", "False"));
                qq.setQuizAssessment(quizAssessment);
                quizAssessment.getQuizQuestions().add(qq);
            });
        }

        if (aiResponse.getShort_answer() != null) {
            aiResponse.getShort_answer().forEach(q -> {
                QuizQuestions qq = new QuizQuestions();
                qq.setQuestionText(q.getQuestion());
                qq.setMarks(2);
                qq.setCorrectAnswer(String.join(", ", q.getKey_points()));
                qq.setOptions(new HashSet<>());
                qq.setQuizAssessment(quizAssessment);
                quizAssessment.getQuizQuestions().add(qq);
            });
        }


        module.addQuizAssessment(quizAssessment);

        courseModuleRepository.save(module);

        log.info("Quiz mapped and saved successfully. Questions count: {}", quizAssessment.getQuizQuestions().size());
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
                                                            q.getOptions(),
                                                            q.getCorrectAnswer()
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

                    ? List.of() : quiz.getQuizQuestions().stream().map(q -> new QuestionDto(q.getQuestionId(), q.getQuestionText(), q.getMarks(), q.getOptions(),q.getCorrectAnswer())).toList();


            return new QuizAssessmentDto(quiz.getTitle(), questionDto);
        }).toList();

        return new ModuleDto(courseModule.getWeek(), courseModule.getModuleName(), courseModule.getContent(), courseModule.getModuleId(), quizDto);
    }

    @Transactional
    public void mapAndSaveCat(GroqQuizResponse aiResponse, Course course, CatAssessment catAssessment) {
        log.info("AI response {}", aiResponse);
      catAssessment.setCourse(course);

        if (catAssessment.getCatQuestions() == null) {
            catAssessment.setCatQuestions(new HashSet<>());
        }

        catAssessment.getCatQuestions().clear();

        if (aiResponse.getMultiple_choice() != null) {
            aiResponse.getMultiple_choice().forEach(q -> {
                CatQuestions qq = new CatQuestions();
                qq.setQuestionText(q.getQuestion());
                qq.setMarks(1);
                qq.setCorrectAnswer(q.getCorrect_answer());
                qq.setOptions(q.getOptions() == null ? new HashSet<>() :
                        new HashSet<>(q.getOptions().values()));
                qq.setCatAssessment(catAssessment);
                catAssessment.getCatQuestions().add(qq);
            });
        }

        if (aiResponse.getTrue_false() != null) {
            aiResponse.getTrue_false().forEach(q -> {
                CatQuestions qq = new CatQuestions();
                qq.setQuestionText(q.getQuestion());
                qq.setMarks(1);
                qq.setCorrectAnswer(Boolean.toString(q.isCorrect_answer()));
                qq.setOptions(Set.of("True", "False"));
                qq.setCatAssessment(catAssessment);
                catAssessment.getCatQuestions().add(qq);
            });
        }

        if (aiResponse.getShort_answer() != null) {
            aiResponse.getShort_answer().forEach(q -> {
                CatQuestions qq = new CatQuestions();
                qq.setQuestionText(q.getQuestion());
                qq.setMarks(2);
                qq.setCorrectAnswer(String.join(", ", q.getKey_points()));
                qq.setOptions(new HashSet<>());
                qq.setCatAssessment(catAssessment);
                catAssessment.getCatQuestions().add(qq);
            });
        }


        course.addCatAssessment(catAssessment);

        courseRepository.save(course);

        log.info("Cat mapped and saved successfully. Questions count: {}", catAssessment.getCatQuestions().size());
    }
}


