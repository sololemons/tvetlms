package com.gradeservice.services;

import com.gradeservice.entities.SubmissionGrade;
import com.gradeservice.repositories.GradeRepository;
import com.shared.dtos.QuestionGradeDto;
import com.shared.dtos.SubmissionGradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionGradeService {

    private final GradeRepository gradeRepository;
   @Transactional
    public List<SubmissionGradeDto> filterGrades(
            String courseId,
            String submissionType,
            String studentAdmissionId,
            String className,
            String submissionId
    ) {

        Specification<SubmissionGrade> spec = Specification.allOf(
                SubmissionGradeSpecification.hasCourseId(courseId),
                SubmissionGradeSpecification.hasSubmissionType(submissionType),
                SubmissionGradeSpecification.hasStudentAdmissionId(studentAdmissionId),
                SubmissionGradeSpecification.hasClassName(className),
                SubmissionGradeSpecification.hasSubmissionId(submissionId)

        );

        return gradeRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "gradedAt"))
                .stream()
                .map(this::toDtoWithQuestionGrades)
                .toList();
    }

    private SubmissionGradeDto toDtoWithQuestionGrades(SubmissionGrade grade) {

        // Map QuestionGrade entity to DTO
        List<QuestionGradeDto> questionGradeDtos = grade.getQuestionGrades().stream()
                .map(q -> new QuestionGradeDto(
                        q.getQuestionId(),
                        q.getQuestionType(),
                        q.getMaxPoints(),
                        q.getAwardedPoints(),
                        q.getIsCorrect(),
                        q.getFeedback()
                ))
                .toList();

        return new SubmissionGradeDto(
                grade.getStudentAdmissionId(),
                grade.getCourseId(),
                grade.getTargetId(),
                grade.getTotalPoints(),
                grade.getMaxPoints(),
                grade.getPercentage(),
                questionGradeDtos
        );
    }
    @Transactional
    public SubmissionGradeDto getSubmissionGrades(String submissionId) {
       SubmissionGrade submissionGrade = gradeRepository.findBySubmissionId(submissionId).orElseThrow(() ->
               new RuntimeException("SubmissionGrade NOt Found"));
       return toDtoWithQuestionGrades(submissionGrade);

    }
}
