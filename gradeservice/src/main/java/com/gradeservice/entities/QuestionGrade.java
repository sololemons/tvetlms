package com.gradeservice.entities;

import com.gradeservice.entities.SubmissionGrade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question_grades")
public class QuestionGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "question_id")
    private String questionId;
    @Column(name = "question_type")
    private String questionType;
    @Column(name = "max_points")
    private double maxPoints;
    @Column(name = "awarded_points")
    private double awardedPoints;
    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ElementCollection
    private List<String> strengths;

    @ElementCollection
    private List<String> improvements;

    @ManyToOne
    @JoinColumn(name = "submission_grade_id")
    private SubmissionGrade submissionGrade;
}
