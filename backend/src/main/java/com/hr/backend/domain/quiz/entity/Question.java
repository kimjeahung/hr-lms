package com.hr.backend.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 문항 - 퀴즈/시험 공용
 * quiz 또는 exam 중 하나만 연결됨
 */
@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;             // 퀴즈 문항일 경우

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;             // 시험 문항일 경우

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false)
    private int score = 10;        // 문항 배점

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<Choice> choices = new ArrayList<>();

    @Builder
    public Question(Quiz quiz, Exam exam, String questionText, int score, int sortOrder) {
        this.quiz         = quiz;
        this.exam         = exam;
        this.questionText = questionText;
        this.score        = score;
        this.sortOrder    = sortOrder;
    }

    public void update(String questionText, int score, int sortOrder) {
        this.questionText = questionText;
        this.score        = score;
        this.sortOrder    = sortOrder;
    }
}
