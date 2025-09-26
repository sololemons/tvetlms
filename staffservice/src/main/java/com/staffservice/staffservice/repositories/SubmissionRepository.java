package com.staffservice.staffservice.repositories;

import com.staffservice.staffservice.entities.Submission;
import com.staffservice.staffservice.entities.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudentAdmissionIdAndAssignmentId(String studentAdmissionId, long assignmentId);

    List<Submission> findByStudentAdmissionId(String studentAdmissionId);

    List<Submission> findByAssignmentId(Long assignmentId);


    List<Submission> findAllBySubmissionStatus(SubmissionStatus submissionStatus);
}
