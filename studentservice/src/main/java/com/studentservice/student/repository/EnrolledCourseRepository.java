package com.studentservice.student.repository;

import com.studentservice.student.entities.EnrolledCourses;
import com.studentservice.student.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public  interface EnrolledCourseRepository extends JpaRepository<EnrolledCourses, Long> {
    boolean existsByStudent_AdmissionIdAndCourseId(String admissionId, long courseId);
    List<EnrolledCourses> findByStudent_AdmissionId(String admissionId);

}
