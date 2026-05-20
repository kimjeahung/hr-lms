package com.hr.backend.domain.quiz.entity;

import com.hr.backend.domain.course.entity.Lecture;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 퀴즈 - 강의(Lecture)마다 1개
 * 퀴즈 통과 → 강의 완료(LectureProgress) 처리
 */
@Entity
@Table(name = "quizzes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long quizId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "pass_score", nullable = false)
    private int passScore = 70;   // 합격 점수 (100점 기준)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<Question> questions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Quiz(Lecture lecture, String title, int passScore) {
        this.lecture   = lecture;
        this.title     = title;
        this.passScore = passScore;
    }

    public void update(String title, int passScore) {
        this.title     = title;
        this.passScore = passScore;
    }
}
