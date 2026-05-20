package com.hr.backend.domain.quiz.entity;

import com.hr.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 응시 결과 - 퀴즈/시험 공용
 * quiz 또는 exam 중 하나만 연결됨
 */
@Entity
@Table(name = "attempts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;             // 퀴즈 응시일 경우

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;             // 시험 응시일 경우

    @Column(nullable = false)
    private int score = 0;

    @Column(name = "pass_score", nullable = false)
    private int passScore = 0;

    @Column(name = "is_passed", nullable = false)
    private boolean passed = false;

    @Column(name = "attempted_at", nullable = false, updatable = false)
    private LocalDateTime attemptedAt;

    @PrePersist
    protected void onCreate() {
        this.attemptedAt = LocalDateTime.now();
    }

    @Builder
    public Attempt(User user, Quiz quiz, Exam exam, int score, int passScore) {
        this.user      = user;
        this.quiz      = quiz;
        this.exam      = exam;
        this.score     = score;
        this.passScore = passScore;
        this.passed    = score >= passScore;
    }
}
