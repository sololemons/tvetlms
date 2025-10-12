package com.studentservice.student.repository;

import com.studentservice.student.entities.EnrolledModules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrolledModulesRepository extends JpaRepository<EnrolledModules, Long> {
    EnrolledModules findByIdAndEnrolledCourse_CourseId(Long moduleId, Long courseId);

    EnrolledModules findByModuleId(Long moduleId);

    List<EnrolledModules> findByEnrolledCourse_CourseId(Long courseId);

    EnrolledModules findByModuleIdAndEnrolledCourse_CourseIdAndEnrolledCourse_Student_AdmissionId(Long moduleId, Long courseId, String admissionId);

}
