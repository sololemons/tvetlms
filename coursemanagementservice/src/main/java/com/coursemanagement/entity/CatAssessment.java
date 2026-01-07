package com.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
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

    @Column(name = "cat_description")
    private String catDescription;

    @Column(name = "duration_minutes")
    private int durationMinutes;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @PrePersist
    @PreUpdate
    private void calculateEndTime() {
        if (startTime != null && durationMinutes > 0) {
            this.endTime = startTime.plusMinutes(durationMinutes);
        }
    }

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

