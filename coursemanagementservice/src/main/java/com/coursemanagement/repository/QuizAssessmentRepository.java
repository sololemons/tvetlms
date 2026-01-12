package com.coursemanagement.repository;

import com.coursemanagement.entity.QuizAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizAssessmentRepository extends JpaRepository<QuizAssessment, Integer> {
    Optional<QuizAssessment> findByModule_Course_CourseIdAndModule_ModuleIdAndAssessmentId(Integer courseId, Integer moduleId, Integer quizId);
}
