package com.gradeservice.repositories;

import com.gradeservice.entities.Certifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationsRepository extends JpaRepository<Certifications, Integer> {
}
