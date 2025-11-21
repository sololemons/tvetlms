package com.studentservice.student.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gamify_data")
public class GamifyData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int gamifyId;
    @Column(name = "week_start")
    private LocalDate weekStart;
    @Column(name = "student_admission_id")
    private String studentAdmissionId;
    @Column(name = "week_end")
    private LocalDate weekEnd;
    @Column(name = "week_points")
    private long weekPoints;
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    @Column(name = "status")
    private Status status;
}
