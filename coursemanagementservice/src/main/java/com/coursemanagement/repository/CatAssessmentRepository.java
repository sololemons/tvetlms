package com.coursemanagement.repository;

import com.coursemanagement.entity.CatAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatAssessmentRepository extends JpaRepository<CatAssessment, Integer> {
}
