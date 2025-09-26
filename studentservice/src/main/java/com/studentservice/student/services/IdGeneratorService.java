package com.studentservice.student.services;

import com.studentservice.student.entities.IdGenerator;
import com.studentservice.student.repository.IdGeneratorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdGeneratorService {

    private final IdGeneratorRepository sequenceRepository;

    @Transactional
    public int generateAdmissionNumber() {
        IdGenerator sequence = sequenceRepository.findById(1L)
                .orElseGet(() -> {
                    IdGenerator idGenerator = new IdGenerator();
                    idGenerator.setId(1L);
                    idGenerator.setLastNumber(999);
                    return sequenceRepository.save(idGenerator);
                });

        int newNumber = sequence.getLastNumber() + 1;
        sequence.setLastNumber(newNumber);
        sequenceRepository.save(sequence);
        return newNumber;
    }
}
