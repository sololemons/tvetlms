package com.coursemanagement.repository;

import com.coursemanagement.entity.Assignments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignments, Long> {
}
