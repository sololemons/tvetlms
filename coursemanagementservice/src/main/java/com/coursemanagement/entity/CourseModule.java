package com.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "module")
public class CourseModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private int moduleId;
    @Column(name = "week")
    private String week;
    @Column(name = "module_name")
    private String moduleName;
    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
