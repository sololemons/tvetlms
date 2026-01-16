package com.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cat_questions")
@Entity
public class CatQuestions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private int questionId;
    @Column(name = "question_text")
    private String questionText;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cat_question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_value")
    @Fetch(FetchMode.SUBSELECT)
    private Set<String> options;

    @Column(name = "correct_answer")
    private String correctAnswer;
    @Column(name = "marks")
    private int marks;


    @ManyToOne
    @JoinColumn(name = "cat_id")
    private CatAssessment catAssessment;


}
