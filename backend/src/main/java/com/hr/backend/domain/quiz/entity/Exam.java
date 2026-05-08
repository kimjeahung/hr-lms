package com.hr.backend.domain.quiz.entity;

import com.hr.backend.domain.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 최종 시험 - 강좌(Course)당 1개
 * 모든 강의 완료 후 응시 가능
 * 시험 통과 → 이수증(Certificate) 발급
 */
@Entity
@Table(name = "exams")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long examId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "pass_score", nullable = false)
    private int passScore = 70;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<Question> questions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Exam(Course course, String title, int passScore) {
        this.course    = course;
        this.title     = title;
        this.passScore = passScore;
    }

    public void update(String title, int passScore) {
        this.title     = title;
        this.passScore = passScore;
    }
}
