package com.gradeservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "submission_grades")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "submission_Id")
    private String submissionId;
    @Column(name = "student_admission_id")
    private String studentAdmissionId;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "target_id")
    private String targetId;
    @Column(name = "module_id")
    private String moduleId;
    @Column(name = "submission_type")
    private String submissionType;
    @Column(name = "total_points")
    private double totalPoints;
    @Column(name = "max_points")
    private double maxPoints;
    @Column(name = "class_name")
    private String className;
    @Column(name = "percentage")
    private double percentage;
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @OneToMany(mappedBy = "submissionGrade", cascade = CascadeType.ALL)
    private List<QuestionGrade> questionGrades;

}
