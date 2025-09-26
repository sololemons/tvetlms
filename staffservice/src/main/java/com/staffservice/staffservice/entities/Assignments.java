package com.staffservice.staffservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Table(name = "assignments")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Assignments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;
    @Column(name = "title")
    private String title;
    @Column(name = "created_date")
    private LocalDateTime creationDate;
    @Column(name = "staff_id")
    private long staffId;
    @Column(name = "description")
    private String description;
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    @Column(name = "marks")
    private int marks;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "assignment_classes",
            joinColumns = @JoinColumn(name = "assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @Fetch(FetchMode.SUBSELECT)
    private Set<Class> classes;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Questions> questions;


}
