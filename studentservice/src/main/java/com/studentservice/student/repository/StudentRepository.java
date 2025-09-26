package com.studentservice.student.repository;

import com.studentservice.student.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {


    @Query("SELECT s FROM Student s WHERE " +
            "(:AdmissionId IS NULL OR s.admissionId = :AdmissionId) AND " +
            "(:AdmissionYear IS NULL OR s.admissionYear = :AdmissionYear)"
            )
    Page<Student> findByFilters(@Param("AdmissionId") Long AdmissionId,
                                   @Param("AdmissionYear") Integer AdmissionYear,
                                    Pageable pageable);

    Optional<Student> findByEmail(String email);

}
