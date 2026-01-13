package com.staffservice.staffservice.entities;

import com.shared.dtos.SubmissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private SubmissionStatus submissionStatus;
    @Column(name = "class_name")
    private String className;
    @Column(name = "submission_type")
    @Enumerated(EnumType.STRING)
    private SubmissionType submissionType;
    @Column(name = "target_id")
    private long targetId;
    @Column(name = "student_admission_id")
    private String studentAdmissionId;
    @Column(name = "course_id")
    private int courseId;
    @Column(name = "module_id")
    private int moduleId;
    @Column(name = "is_submitted")
    private boolean isSubmitted;
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionFile> files;


}
