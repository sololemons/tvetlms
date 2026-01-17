package com.coursemanagement.repository;

import com.coursemanagement.entity.Assignments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignments, Long> {
    Optional<Assignments> findByAssignmentIdAndCourse_CourseId(Long assignmentId,Integer courseId);
}
