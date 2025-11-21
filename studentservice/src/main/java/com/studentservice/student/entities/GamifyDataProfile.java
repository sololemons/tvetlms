package com.studentservice.student.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gamify_data_profile")
public class GamifyDataProfile {
    @Id
    @Column(name = "gamify_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gamifyId;

    @Column(name = "total_points")
    private long totalPoints;

    @ManyToOne
    @JoinColumn(name = "student_admission_id", referencedColumnName = "admission_id")
    private Student student;

    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "badge_count")
    private Map<GamifyBadges, Integer> badgesAcquired = new HashMap<>();
}
