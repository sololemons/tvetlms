package com.studentservice.student.repository;

import com.studentservice.student.entities.GamifyData;
import com.studentservice.student.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GamifyDataRepository extends JpaRepository<GamifyData, Long> {

    List<GamifyData> findByStudentAdmissionId(String admissionId);

    GamifyData findGamifyDataByStudentAdmissionIdAndStatus(String studentAdmissionId, Status status);

    List<GamifyData> findByStatus(Status status);
}
