package com.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cat")

public class CatAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cat_id")
    private int catId;

    @Column(name = "title")
    private String title;

    @Column(name = "duration_minutes")
    private int durationMinutes;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

   // @Column(name = "total_marks")
   // private int totalMarks;

    @OneToMany(mappedBy = "catAssessment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude
    private Set<CatQuestions> catQuestions;



    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
