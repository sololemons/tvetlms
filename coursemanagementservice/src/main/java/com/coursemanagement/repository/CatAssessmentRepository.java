package com.coursemanagement.repository;

import com.coursemanagement.entity.CatAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatAssessmentRepository extends JpaRepository<CatAssessment, Integer> {
    Optional<CatAssessment> findByCourse_CourseIdAndCatId(Integer courseId, Integer catId);
}
