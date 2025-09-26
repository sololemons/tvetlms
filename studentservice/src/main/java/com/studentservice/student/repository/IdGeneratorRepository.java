package com.studentservice.student.repository;

import com.studentservice.student.entities.IdGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdGeneratorRepository extends JpaRepository<IdGenerator, Long> {
}
