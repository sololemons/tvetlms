package com.studentservice.student.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gamify_data")
public class GamifyData {
    @Id
    @Column(name = "gamify_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gamifyId;
    @Column(name = "student_name")
    private String studentName;
    @Column(name = "student_id")
    private int studentId;
    @Column(name = "total_minutes")
    private long totalMinutes;

}
