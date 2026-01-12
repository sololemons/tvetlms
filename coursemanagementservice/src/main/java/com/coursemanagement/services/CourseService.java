package com.coursemanagement.services;

import com.coursemanagement.configuration.GroqClient;
import com.coursemanagement.configuration.RabbitMQConfiguration;
import com.coursemanagement.dtos.*;
import com.coursemanagement.entity.*;
import com.coursemanagement.repository.*;
import com.coursemanagement.utility.MapperServices;
import com.shared.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroqClient groqClient;
    private final MapperServices mapperServices;
    private final CatAssessmentRepository catAssessmentRepository;
    private final QuizAssessmentRepository quizAssessmentRepository;

    public String createCourse(CourseDto courseDto) {
        Course course = new Course();
        course.setCourseName(courseDto.getCourseName());
        course.setDescription(courseDto.getDescription());

        CourseOverview courseOverview = new CourseOverview();
        courseOverview.setDuration(courseDto.getCourseOverview().getDuration());
        course.setCourseOverview(courseOverview);

        courseRepository.save(course);
        return "Course" + course.getCourseName() + "Created Successfully";

    }

    public String createModule(CreateModuleDto createModuleDto) {
        Course course = courseRepository.findById(createModuleDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID " + createModuleDto.getCourseId()));

        CourseModule courseModule = new CourseModule();
        courseModule.setModuleName(createModuleDto.getModuleName());
        courseModule.setWeek(createModuleDto.getWeek());
        courseModule.setContent(createModuleDto.getContent());
        courseModule.setCourse(course);

        courseModuleRepository.save(courseModule);
        return "Module" + courseModule.getModuleName() + "Created Successfully In Course with Id" + createModuleDto.
                getCourseId();

    }

    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAllWithAssociations();
        return courses.stream()
                .map(mapperServices::mapToDto)
                .toList();

    }


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

    @Transactional
    public List<ModuleDto> getModules(Integer courseId) {
        List<CourseModule> courseModule = courseModuleRepository.findByCourse_CourseId(courseId);
        return courseModule.stream()
                .map(this.mapperServices::mapToDtoModule)
                .toList();

    }


    public String updateCourseInfo(UpdateCourseDto updateCourseDto) {
        Optional<Course> courseInfo = courseRepository.findById(updateCourseDto.getCourseId());

        if (courseInfo.isPresent()) {
            Course course = courseInfo.get();
            course.setCourseName(updateCourseDto.getCourseName());
            course.setDescription(updateCourseDto.getDescription());
            courseRepository.save(course);

        }
        return "Course Updated Successfully";

    }

    @Transactional
    public String generateQuiz(QuizGenerationRequest quizGenerationRequest) {

        CourseModule module = courseModuleRepository
                .findByCourse_CourseIdAndModuleId(
                        quizGenerationRequest.getCourseId(),
                        quizGenerationRequest.getModuleId()
                )
                .orElseThrow(() -> new IllegalArgumentException("Module not found"));
        QuizAssessment quizAssessment = quizAssessmentRepository.
                findById(quizGenerationRequest.getQuizId()).orElseThrow(() -> new RuntimeException("Quiz Not Found"));
        AiQuizRequest aiQuizRequest = new AiQuizRequest(
                module.getContent(),
                quizGenerationRequest.getDifficulty(),
                quizGenerationRequest.getNoOfCloseEndedQuestions(),
                quizGenerationRequest.getNoOfTrueFalseQuestions(),
                quizGenerationRequest.getNoOfOpenEndedQuestions(),
                quizGenerationRequest.getNoOfOptions()
        );
        log.info("AI request {}", aiQuizRequest);
        GroqQuizResponse aiResponse = groqClient.generateQuiz(aiQuizRequest);
        log.info("AI response {}", aiResponse);

        mapperServices.mapAndSaveQuiz(aiResponse, module, quizAssessment);

        return "Quiz Generated Successfully";
    }

    @RabbitListener(queues = RabbitMQConfiguration.ASSIGN_COURSES)
    private void assignClassNamesToCourses(AssignCourseDto assignCourseDto) {
        Course course = courseRepository.findById(assignCourseDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID " + assignCourseDto.getCourseId()));

        course.setAssignedCourseNames(assignCourseDto.getClassName());

        courseRepository.save(course);

    }
    @Transactional
    public String generateCatAssessment(CatGenerationRequest catGenerationRequest) {

        CourseModule module = courseModuleRepository.
                findByCourse_CourseIdAndStatus(catGenerationRequest.getCourseId(),ModuleStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Module not found"));
        Course course = courseRepository.findByCourseId(catGenerationRequest.getCourseId()).orElseThrow(() ->
                new IllegalArgumentException("Course Not found"));
        CatAssessment catAssessment = catAssessmentRepository.
                findById(catGenerationRequest.getCatId()).orElseThrow(() -> new RuntimeException("Cat Not Found"));
        AiCatRequest aiCatRequest = new AiCatRequest(
                module.getContent(),
                catGenerationRequest.getDifficulty(),
                catGenerationRequest.getNoOfCloseEndedQuestions(),
                catGenerationRequest.getNoOfTrueFalseQuestions(),
                catGenerationRequest.getNoOfOpenEndedQuestions(),
                catGenerationRequest.getNoOfOptions()
        );
        log.info("AI request {}", aiCatRequest);
        GroqQuizResponse aiResponse = groqClient.generateCat(aiCatRequest);
        log.info("AI response {}", aiResponse);

        mapperServices.mapAndSaveCat(aiResponse, course, catAssessment);

        return "Quiz Generated Successfully";

    }


    @Transactional
    public String createCat(CreateCatDto dto) {

        Course course = courseRepository.findByCourseId(dto.getCourseId()).orElseThrow(() -> new IllegalArgumentException("Course Not found"));

        CatAssessment cat = new CatAssessment();
        cat.setTitle(dto.getCatTitle());
        cat.setDurationMinutes(dto.getDurationMinutes());
        cat.setStartTime(LocalDateTime.parse(dto.getStartTime()));
        cat.setCatDescription(dto.getCatDescription());
        cat.setCourse(course);

        course.getCats().add(cat);

        courseRepository.save(course);

        return "Cat Created For course with id " + cat.getCourse().getCourseId();
    }

    @Transactional
    public String createQuiz(CreateQuizDto createQuizDto) {

        Course course = courseRepository.findByCourseId(createQuizDto.getCourseId()).orElseThrow(() -> new IllegalArgumentException("Course Not found"));


        CourseModule module = courseModuleRepository
                .findByCourse_CourseIdAndModuleId(
                        createQuizDto.getCourseId(),
                        createQuizDto.getModuleId()
                )
                .orElseThrow(() -> new IllegalArgumentException("Module not found"));

        QuizAssessment quiz = new QuizAssessment();
        quiz.setTitle(createQuizDto.getQuizTitle());
        quiz.setQuizAssessmentDescription(createQuizDto.getQuizDescription());
        quiz.setDueDate(LocalDateTime.parse(createQuizDto.getDueDate()));
        quiz.setModule(module);

        module.getQuizAssessments().add(quiz);

        courseModuleRepository.save(module);

        return "Quiz created successfully with course Id " + createQuizDto.getCourseId() + "and Module Id " + createQuizDto.getModuleId();
    }

    @Transactional
    public String createAssignment(CreateAssignmentDto dto) {

        Course course = courseRepository.findByCourseId(Math.toIntExact(dto.getCourseId()))
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Assignments assignment = new Assignments();
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setDueDate(LocalDateTime.parse(dto.getDueDate()));
        assignment.setTotalMarks(dto.getTotalMarks());

        assignment.setAllowDocuments(dto.isAllowDocuments());
        assignment.setAllowImages(dto.isAllowImages());
        assignment.setAllowVideos(dto.isAllowVideos());
        assignment.setMaxFileSizeMb(dto.getMaxFileSizeMb());

        assignment.setCourse(course);

        assignmentRepository.save(assignment);

        return "Assignment created successfully";
    }


    public CourseDto getFullCourse(Integer courseId) {

        Course course = courseRepository.findByCourseId(courseId).orElseThrow(() -> new IllegalArgumentException("Course Not found"));

        return mapperServices.mapToDto(course);

    }

    public CourseDto getActiveCourses(Integer courseId) {

        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Set<CourseModule> activeModules = course.getModules().stream()
                .filter(m -> m.getStatus() == ModuleStatus.ACTIVE)
                .collect(Collectors.toSet());

        course.setModules(activeModules);

        return mapperServices.mapToDto(course);
    }


    public String activateModule(ActivateModuleDto activateModuleDto) {
        CourseModule courseModule = courseModuleRepository.
                findByCourse_CourseIdAndModuleId
                        (activateModuleDto.getCourseId(),activateModuleDto.getModuleId()).orElseThrow(() ->
                        new RuntimeException("Module not found"));
        courseModule.setStatus(ModuleStatus.ACTIVE);
        courseModuleRepository.save(courseModule);
        return "Module with Id" + activateModuleDto.getModuleId() +"activated Successfully";
    }

    public QuizAssessmentResponseDto getQuizAssessmentDto(
            Integer courseId,
            Integer moduleId,
            Integer quizId
    ) {

        QuizAssessment quizAssessment = quizAssessmentRepository
                .findByModule_Course_CourseIdAndModule_ModuleIdAndAssessmentId(
                        courseId,
                        moduleId,
                        quizId
                )
                .orElseThrow(() -> new RuntimeException("Quiz assessment not found"));

        return new QuizAssessmentResponseDto(
                quizAssessment.getTitle(),
                quizAssessment.getQuizQuestions() == null
                        ? List.of()
                        : quizAssessment.getQuizQuestions()
                        .stream()
                        .map(q -> new QuestionDto(
                                q.getQuestionId(),
                                q.getQuestionText(),
                                q.getMarks(),
                                q.getOptions()
                        ))
                        .toList()
        );
    }


}
