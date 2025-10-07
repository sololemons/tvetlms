package com.studentservice.student.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enrolled_modules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledModules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "duration")
    private String duration;
    @Column(name = "module_name")
    private String moduleName;
    @Column(name = "module_id")
    private Long moduleId;
    @Column(name = "is_completed")
    private boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "enrolled_course_id")
    private EnrolledCourses enrolledCourse;
}
