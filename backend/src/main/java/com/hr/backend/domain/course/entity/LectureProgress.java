package com.hr.backend.domain.course.entity;

import com.hr.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 강의 단위 완료 여부 추적
 * 영상 시청 완료 + 퀴즈 통과 시 완료 처리
 */
@Entity
@Table(name = "lecture_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "lecture_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public LectureProgress(User user, Lecture lecture) {
        this.user    = user;
        this.lecture = lecture;
    }

    public void complete() {
        this.completed   = true;
        this.completedAt = LocalDateTime.now();
    }
}
