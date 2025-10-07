package com.studentservice.student.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "enrolled_courses")
public class EnrolledCourses {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "course_name")
    private String courseName;
    @Column(name = "progression")
    private String progression;
    @Column(name = "course_id")
    private long courseId;
    @Column(name = "is_completed")
    private boolean isCompleted;
    @ManyToOne
    @JoinColumn(name = "student_admission_id", referencedColumnName = "admission_id")
    private Student student;

}
