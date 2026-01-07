package com.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "submission")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private int submissionId;
    @Column(name = "submission_text")
    private String submissionText;
    @Column(name = "submission_date")
    private String submissionDate;
    @Column(name = "submission_status")
    private SubmissionStatus submissionStatus;
    @Column(name = "class_name")
    private String className;
    @Column(name = "student_admission_id")
    private String studentAdmissionId;
    @Column(name = "is_submitted")
    private boolean isSubmitted;
    @Column(name = "assignment_id")
    private long assignmentId;

}
