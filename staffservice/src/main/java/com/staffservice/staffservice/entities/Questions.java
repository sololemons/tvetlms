package com.staffservice.staffservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "questions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;
    @Column(name = "question_text")
    private String questionText;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_value")
    @Fetch(FetchMode.SUBSELECT)
    private Set<String> options;

    @Column(name = "correct_answer")
    private String correctAnswer;
    @Column(name = "marks")
    private int marks;
    @Column(name = "question_type")
    private QuestionType questionType;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignments assignment;
}
