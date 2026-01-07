package com.studentservice.student.dtos;

import com.studentservice.student.entities.GamifyBadges;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamifyProfilesDto {
    private long totalPoints;
    private String studentName;
    private Map<GamifyBadges, Integer> badgesAcquired = new HashMap<>();


}
