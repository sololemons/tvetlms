package com.staffservice.staffservice.repositories;

import com.shared.dtos.SubmissionType;
import com.staffservice.staffservice.entities.Submission;
import com.staffservice.staffservice.entities.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
  //  Optional<Submission> findByStudentAdmissionIdAndAssignmentId(String studentAdmissionId, long assignmentId);

    List<Submission> findByStudentAdmissionId(String studentAdmissionId);



    List<Submission> findAllBySubmissionStatus(SubmissionStatus submissionStatus);

    Optional<Submission> findByStudentAdmissionIdAndTargetIdAndSubmissionType(String studentAdmissionId, Long targetId, SubmissionType submissionType);

    Optional<Submission> findByStudentAdmissionIdAndTargetIdAndSubmissionTypeAndCourseId(String studentAdmissionId, long catId, SubmissionType submissionType, int courseId);


    List<Submission> findBySubmissionStatusAndSubmissionType(SubmissionStatus submissionStatus,SubmissionType submissionType);



    Optional<Submission>findByIdAndSubmissionStatus(Long submissionId, SubmissionStatus submissionStatus);
}
