package com.coursemanagement.repository;

import com.coursemanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.cats cat " +
            "LEFT JOIN FETCH cat.catQuestions cq " +
            "LEFT JOIN FETCH cq.options " +
            "LEFT JOIN FETCH c.modules m " +
            "LEFT JOIN FETCH m.quizAssessments qa " +
            "LEFT JOIN FETCH qa.quizQuestions qq " +
            "LEFT JOIN FETCH qq.options")
    List<Course> findAllWithAssociations();


}
