package com.gradeservice.services;

import com.gradeservice.dtos.SubmissionGradeDto;
import com.gradeservice.entities.SubmissionGrade;
import com.gradeservice.repositories.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionGradeService {

    private final GradeRepository gradeRepository;
    public List<SubmissionGradeDto> filterGrades(
            String courseId,
            String submissionType,
            String studentAdmissionId,
            String className
    ) {

        Specification<SubmissionGrade> spec = Specification.allOf(
                SubmissionGradeSpecification.hasCourseId(courseId),
                SubmissionGradeSpecification.hasSubmissionType(submissionType),
                SubmissionGradeSpecification.hasStudentAdmissionId(studentAdmissionId),
                SubmissionGradeSpecification.hasClassName(className)
        );

        return gradeRepository
                .findAll(spec, Sort.by(Sort.Direction.DESC, "gradedAt"))
                .stream()
                .map(this::toDto)
                .toList();
    }
    private SubmissionGradeDto toDto(SubmissionGrade grade) {
        return new SubmissionGradeDto(
                grade.getStudentAdmissionId(),
                grade.getCourseId(),
                grade.getTargetId(),
                grade.getTotalPoints(),
                grade.getMaxPoints(),
                grade.getPercentage(),
                grade.getGradedAt()
        );
    }
}
