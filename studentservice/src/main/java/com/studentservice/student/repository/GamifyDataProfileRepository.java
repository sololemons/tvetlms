package com.studentservice.student.repository;

import com.studentservice.student.entities.GamifyDataProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GamifyDataProfileRepository extends JpaRepository<GamifyDataProfile, Long> {
    Optional<GamifyDataProfile> findByStudent_AdmissionId(String admissionId);
    Optional<GamifyDataProfile> findByStudent_Email(String email);


}
