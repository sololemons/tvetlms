package com.coursemanagement.services;

import com.coursemanagement.configuration.GroqClient;
import com.coursemanagement.configuration.RabbitMQConfiguration;
import com.coursemanagement.dtos.*;
import com.coursemanagement.entity.*;
import com.coursemanagement.repository.CourseModuleRepository;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.utility.MapperServices;
import com.shared.dtos.AssignCourseDto;
import com.shared.dtos.ModuleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final GroqClient groqClient;
    private MapperServices mapperServices;

    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAllWithAssociations();
      return   courses.stream()
                .map(course -> mapperServices.mapToDto(course))
                .toList();

    }

   // Add Course MANUALLY
    public String addCourses(CourseDto courseDto) {

        Course course = new Course();
        course.setCourseName(courseDto.getCourseName());
        course.setDescription(courseDto.getDescription());

        CourseOverview courseOverview = new CourseOverview();
        courseOverview.setDuration(courseDto.getCourseOverview().getDuration());
        course.setCourseOverview(courseOverview);


        if (courseDto.getCatAssessmentDto() != null) {

            Set<CatAssessment> catAssessments = courseDto.getCatAssessmentDto().stream().map(dto -> {

                CatAssessment catAssessment = new CatAssessment();
                catAssessment.setTitle(dto.getTitle());
                catAssessment.setDurationMinutes(dto.getDurationMinutes());
                catAssessment.setStartTime(dto.getStartTime());
                catAssessment.setEndTime(dto.getEndTime());
                catAssessment.setCourse(course);

                if (dto.getQuestions() != null) {
                    Set<CatQuestions> questions = dto.getQuestions().stream().map(qDto -> {
                        CatQuestions q = new CatQuestions();
                        q.setQuestionText(qDto.getText());
                        q.setOptions(qDto.getOptions());
                        q.setMarks(qDto.getMarks());
                        q.setCatAssessment(catAssessment);
                        return q;
                    }).collect(Collectors.toSet());

                    catAssessment.setCatQuestions(questions);
                }

                return catAssessment;
            }).collect(Collectors.toSet());

            course.setCats(catAssessments);
        }


        if (courseDto.getModuleDto() != null) {

            Set<CourseModule> modules = courseDto.getModuleDto().
                    stream().
                    map
                            (dto -> {

                CourseModule module = new CourseModule();
                module.setWeek(dto.getWeek());
                module.setModuleName(dto.getModuleName());
                module.setContent(dto.getContent());
                module.setCourse(course);

                if (dto.getQuizAssessmentDto() != null) {

                    Set<QuizAssessment> quizAssessments = dto.getQuizAssessmentDto().
                            stream().
                            map(quizDto -> {

                        QuizAssessment quizAssessment = new QuizAssessment();
                        quizAssessment.setTitle(quizDto.getTitle());
                        quizAssessment.setModule(module);

                        if (quizDto.getQuestions() != null) {
                            Set<QuizQuestions> quizQuestions = quizDto.getQuestions().stream().map(qDto -> {
                                QuizQuestions qq = new QuizQuestions();
                                qq.setQuestionText(qDto.getText());
                                qq.setOptions(qDto.getOptions());
                                qq.setMarks(qDto.getMarks());
                                qq.setQuizAssessment(quizAssessment);
                                return qq;
                            }).collect(Collectors.toSet());

                            quizAssessment.setQuizQuestions(quizQuestions);
                        }

                        return quizAssessment;
                    }).collect(Collectors.toSet());

                    module.setQuizAssessments(quizAssessments);
                }

                return module;
            }).collect(Collectors.toSet());

            course.setModules(modules);
        }

        courseRepository.save(course);
        return "Course added successfully!";
    }


    public List<ModuleDto> getModules(Integer courseId) {
        List<CourseModule> courseModule = courseModuleRepository.findByCourse_CourseId(courseId);
        return courseModule.stream()
                .map(module -> this.mapperServices.mapToDtoModule(module))
                .toList();

    }





    public String updateCourseInfo(UpdateCourseDto updateCourseDto) {
        Optional<Course> courseInfo = courseRepository.findById(updateCourseDto.getCourseId());

        if (courseInfo.isPresent()){
            Course course = courseInfo.get();
            course.setCourseName(updateCourseDto.getCourseName());
            course.setDescription(updateCourseDto.getDescription());
            courseRepository.save(course);

        }
        return "Course Updated Successfully";

    }

    public String generateQuiz(QuizGenerationRequest quizGenerationRequest) {

        CourseModule module = courseModuleRepository.findByCourse_CourseIdAndModuleId(quizGenerationRequest.getCourseId(),quizGenerationRequest.getModuleId())
                .orElseThrow(() -> new IllegalArgumentException("Module not found"));

        String prompt = buildPrompt(
                module,
                quizGenerationRequest.getNoOfQuestions(),
                quizGenerationRequest.getDifficulty()
        );

        GroqQuizResponse aiResponse = groqClient.generateQuiz(prompt);

        mapperServices.mapAndSaveQuiz(aiResponse, module);
        return "Quiz Generated Successfully";
    }

    private String buildPrompt(CourseModule module, int numberOfQuestions, String difficulty) {

        return """
    You are an AI quiz generator for a TVET LMS.

    Generate %d multiple-choice questions.

    Difficulty: %s

    Rules:
    - 4 options per question
    - Include correctAnswer
    - Include marks
    - Output JSON only

    Module content:
    %s
    """.formatted(numberOfQuestions, difficulty, module.getContent());
    }

    @RabbitListener(queues = RabbitMQConfiguration.ASSIGN_COURSES)
    private void assignClassNamesToCourses(AssignCourseDto assignCourseDto){
        Course course = courseRepository.findById(assignCourseDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID " + assignCourseDto.getCourseId()));

        course.setAssignedCourseNames(assignCourseDto.getClassName());

        courseRepository.save(course);

    }


}
