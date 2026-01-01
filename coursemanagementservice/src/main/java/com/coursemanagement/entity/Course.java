package com.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @EqualsAndHashCode.Exclude
    private Set<CourseModule> modules = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Set<CatAssessment> cats = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "course_assigned_names", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "course_name")
    private Set<String> assignedCourseNames = new HashSet<>();
}
