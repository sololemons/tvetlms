package com.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Subselect;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private int courseId;
    @Column(name = "course_name")
    private String courseName;
    @Column(name = "description")
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "overview_id", referencedColumnName = "id")
    private CourseOverview courseOverview;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<CourseModule> modules = new ArrayList<>();
}
