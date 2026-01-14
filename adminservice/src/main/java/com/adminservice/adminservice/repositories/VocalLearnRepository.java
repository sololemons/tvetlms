package com.adminservice.adminservice.repositories;

import com.adminservice.adminservice.entities.VocalLearnSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocalLearnRepository extends JpaRepository<VocalLearnSignature,Long> {
}
