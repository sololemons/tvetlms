package com.staffservice.staffservice.repositories;

import com.staffservice.staffservice.entities.Assignments;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignments, Long> {
   // @EntityGraph(attributePaths = {"classes", "questions", "questions.options"})
    List<Assignments> findByClasses_ClassName(String className);
   // @EntityGraph(attributePaths = {"classes", "questions","questions.options"})
    List<Assignments> findByStaffId(Long staffId);





}
