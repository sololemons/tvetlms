package com.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
    @Table(name = "assignment")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Assignments {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long assignmentId;

        private String title;

        @Column(columnDefinition = "TEXT")
        private String description;

        private LocalDateTime dueDate;

        private int totalMarks;

        private boolean allowDocuments;
        private boolean allowImages;
        private boolean allowVideos;

        private long maxFileSizeMb;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "course_id")
        private Course course;
    }


