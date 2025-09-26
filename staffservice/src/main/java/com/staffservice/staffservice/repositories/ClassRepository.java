package com.staffservice.staffservice.repositories;

import com.staffservice.staffservice.entities.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {

   Set<Class> findByClassNameIn(List<String> className);

}
