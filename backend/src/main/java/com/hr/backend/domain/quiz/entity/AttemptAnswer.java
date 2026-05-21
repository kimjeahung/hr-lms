package com.hr.backend.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 응시 문항별 답안 — attempts 1건당 N개
 * 유저가 각 문항에서 어떤 선택지를 골랐는지, 정답인지 기록
 */
@Entity
@Table(name = "attempt_answers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private Attempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** 유저가 선택한 선택지 (미응답이면 null) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "choice_id")
    private Choice choice;

    /** 채점 시점의 정답 여부 스냅샷 */
    @Column(name = "is_correct", nullable = false)
    private boolean correct = false;

    @Builder
    public AttemptAnswer(Attempt attempt, Question question, Choice choice, boolean correct) {
        this.attempt  = attempt;
        this.question = question;
        this.choice   = choice;
        this.correct  = correct;
    }

    /**
     * 채점 단계에서 Attempt FK 없이 임시로 담아두는 값 객체
     * AttemptService.grade()가 반환하고, saveAttemptAnswers()에서 엔티티로 변환
     */
    public record Draft(Question question, Choice choice, boolean correct) {}
}
