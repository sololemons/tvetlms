package com.studentservice.student.utilis;

import com.studentservice.student.entities.*;
import com.studentservice.student.exceptions.UserNotFoundException;
import com.studentservice.student.repository.GamifyDataProfileRepository;
import com.studentservice.student.repository.GamifyDataRepository;
import com.studentservice.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GamifyScheduler {
    private final GamifyDataRepository gamifyDataRepository;
    private final StudentRepository studentRepository;
    private final GamifyDataProfileRepository gamifyDataProfileRepository;
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void processWeeklyGamification() {

        LocalDateTime now = LocalDateTime.now();

        List<GamifyData> activeWeeks = gamifyDataRepository.findByStatus(Status.ACTIVE);

        for (GamifyData activeWeek : activeWeeks) {

            if (activeWeek.getWeekEnd().isAfter(ChronoLocalDate.from(now.plusMinutes(30)))) {
                continue;
            }

            String studentAdmissionId = activeWeek.getStudentAdmissionId();
            Student student = studentRepository.findByAdmissionId(studentAdmissionId).orElseThrow(() -> new UserNotFoundException("Student not found with email: " + studentAdmissionId));


            long weeklyPoints = activeWeek.getWeekPoints();

            long finalWeeklyPoints = applyWeeklyAlgorithm(weeklyPoints);

            GamifyBadges badge = determineBadge(finalWeeklyPoints);

            GamifyDataProfile profile = gamifyDataProfileRepository
                    .findByStudent_AdmissionId(studentAdmissionId)
                    .orElseGet(() -> {
                        GamifyDataProfile newProfile = new GamifyDataProfile();
                        newProfile.setStudent(student);
                        newProfile.setTotalPoints(0);
                        newProfile.setBadgesAcquired(new HashMap<>());
                        return newProfile;
                    });


            profile.setTotalPoints(profile.getTotalPoints() + finalWeeklyPoints);
            Map<GamifyBadges, Integer> badges = profile.getBadgesAcquired();
            badges.put(badge, badges.getOrDefault(badge, 0) + 1);
            profile.setBadgesAcquired(badges);
            gamifyDataProfileRepository.save(profile);

            activeWeek.setStatus(Status.INACTIVE);
            gamifyDataRepository.save(activeWeek);
        }
    }

    private long applyWeeklyAlgorithm(long points) {
        return Math.min(points, 1000);
    }

    private GamifyBadges determineBadge(long points) {
        if (points >= 800) return GamifyBadges.MASTER_MIND;
        if (points >= 600) return GamifyBadges.LEARNING_CHAMPION;
        if (points >= 400) return GamifyBadges.FOCUSED_SILVER;
        if (points >= 200) return GamifyBadges.STUDY_SPRINTER;
        return GamifyBadges.TIME_EXPLORER;
    }



}
