package com.gradeservice.repositories;

import com.gradeservice.entities.SubmissionGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends
        JpaRepository<SubmissionGrade, Long>,
        JpaSpecificationExecutor<SubmissionGrade> {
   Optional<SubmissionGrade>findBySubmissionId(String submissionId);
}
