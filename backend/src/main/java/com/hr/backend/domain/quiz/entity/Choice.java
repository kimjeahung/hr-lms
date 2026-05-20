package com.hr.backend.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 선택지 - 퀴즈/시험 문항 공용
 */
@Entity
@Table(name = "choices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long choiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_quiz_choice_question"))
    private Question question;

    @Column(name = "choice_text", nullable = false, length = 300)
    private String choiceText;

    @Column(name = "is_correct", nullable = false)
    private boolean correct = false;   // 정답 여부

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Builder
    public Choice(Question question, String choiceText, boolean correct, int sortOrder) {
        this.question   = question;
        this.choiceText = choiceText;
        this.correct    = correct;
        this.sortOrder  = sortOrder;
    }

    public void update(String choiceText, boolean correct, int sortOrder) {
        this.choiceText = choiceText;
        this.correct    = correct;
        this.sortOrder  = sortOrder;
    }
}
