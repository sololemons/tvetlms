package com.staffservice.staffservice.repositories;

import com.staffservice.staffservice.entities.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByEmail(String email);


    @Query("SELECT s FROM Staff s WHERE " +
            "(:staffId IS NULL OR s.staffId = :staffId) AND " +
            "(:admissionYear IS NULL OR s.admissionYear = :admissionYear) AND " +
            "(:department IS NULL OR s.department = :department)")
    Page<Staff> findByFilters(@Param("staffId") Long staffId,
                              @Param("admissionYear") Long admissionYear,
                              @Param("department") String department,
                              Pageable pageable);

}
