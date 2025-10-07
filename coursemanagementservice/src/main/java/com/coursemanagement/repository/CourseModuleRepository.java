package com.coursemanagement.repository;

import com.coursemanagement.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Integer> {

    List<CourseModule> findByCourse_CourseId(Integer courseId);

}
