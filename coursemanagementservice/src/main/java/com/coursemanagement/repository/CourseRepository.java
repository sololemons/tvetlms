package com.coursemanagement.repository;

import com.coursemanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.modules")
    List<Course> findAllWithModules();

}
