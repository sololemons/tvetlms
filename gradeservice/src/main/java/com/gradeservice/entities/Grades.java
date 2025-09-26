package com.gradeservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grades")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Grades {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "admission_id")
    private String admissionId;
    @Column(name = "grade")
    private String grade;
    @Column(name = "course_name")
    private String courseName;
    @Column(name = "year")
    private String year;
    @Column(name = "class_name")
    private String className;
    @Column(name = "assessment_name")
    private String assessmentName;
}
