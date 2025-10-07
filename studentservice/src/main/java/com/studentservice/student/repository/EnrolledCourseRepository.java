package com.studentservice.student.repository;

import com.studentservice.student.entities.EnrolledCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface EnrolledCourseRepository extends JpaRepository<EnrolledCourses, Long> {
}
