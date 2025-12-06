package com.studentservice.student.services;

import com.studentservice.student.dtos.GamifyPointsDto;
import com.studentservice.student.entities.*;
import com.studentservice.student.exceptions.MissingFieldException;
import com.studentservice.student.exceptions.UserNotFoundException;
import com.studentservice.student.repository.GamifyDataProfileRepository;
import com.studentservice.student.repository.GamifyDataRepository;
import com.studentservice.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GamifyServices {
    private final GamifyDataRepository gamifyDataRepository;
    private final StudentRepository studentRepository;
    private final GamifyDataProfileRepository gamifyDataProfileRepository;
    public String updateGamifyPoints(GamifyPointsDto dto) {
        deactivateExpiredWeeks(dto.getAdmissionId());
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime newWeekStart = now;
        LocalDateTime newWeekEnd = now.plusHours(1);

        List<GamifyData> records =
                gamifyDataRepository.findByStudentAdmissionId(dto.getAdmissionId());

        GamifyData activeWeek = null;

        for (GamifyData record : records) {
            boolean withinRange =
                    record.getStatus() == Status.ACTIVE &&
                    !now.isBefore(record.getWeekStart()) &&
                            !now.isAfter(record.getWeekEnd());

            if (withinRange) {
                activeWeek = record;
                break;
            }
        }

        if (activeWeek != null) {
            activeWeek.setWeekPoints(activeWeek.getWeekPoints() + dto.getPoints());
            activeWeek.setUpdatedAt(now.toLocalDate());
            gamifyDataRepository.save(activeWeek);
            return "Points updated in current active period.";
        }

        GamifyData gamifyData = new GamifyData();
        gamifyData.setStudentAdmissionId(dto.getAdmissionId());
        gamifyData.setWeekStart(newWeekStart);
        gamifyData.setWeekEnd(newWeekEnd);
        gamifyData.setWeekPoints(dto.getPoints());
        gamifyData.setUpdatedAt(now.toLocalDate());
        gamifyData.setStatus(Status.ACTIVE);

        gamifyDataRepository.save(gamifyData);

        return "New period created and points added.";
    }

    public GamifyData getActiveWeek(Principal principal) {

        String email = principal.getName();

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Student not found with email: " + email));

        String admissionId = student.getAdmissionId();

        GamifyData activeWeekData = gamifyDataRepository
                .findGamifyDataByStudentAdmissionIdAndStatus(admissionId, Status.ACTIVE);

        if (activeWeekData != null) {
            return activeWeekData;
        }

        throw new MissingFieldException("Active gamify week data not found for student: " + admissionId);
    }

    public GamifyDataProfile getGamifyProfile(Principal principal) {
        String email = principal.getName();

        return gamifyDataProfileRepository
                .findByStudent_Email(email)
                .orElseThrow(() -> new UserNotFoundException("Gamify profile not found"));
    }

    public void deactivateExpiredWeeks(String admissionId) {

        LocalDateTime today = LocalDateTime.now();

        List<GamifyData> records =
                gamifyDataRepository.findByStudentAdmissionId(admissionId);

        for (GamifyData record : records) {

            if (record.getWeekEnd().isBefore(today) &&
                    record.getStatus() == Status.ACTIVE) {

                record.setStatus(Status.INACTIVE);
                gamifyDataRepository.save(record);
            }
        }
    }



}
