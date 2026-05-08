package com.hr.backend.domain.course.entity;

import com.hr.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 영상 시청 로그
 * session_started_at / session_ended_at 으로 배속 감지
 * (실제 경과시간 vs duration_sec 비교)
 */
@Entity
@Table(name = "video_watch_logs",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "video_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoWatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private CourseVideo video;

    @Column(name = "watched_sec", nullable = false)
    private int watchedSec = 0;       // 누적 시청 초

    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

    @Column(name = "session_started_at")
    private LocalDateTime sessionStartedAt;  // 시청 시작 시각

    @Column(name = "session_ended_at")
    private LocalDateTime sessionEndedAt;    // 시청 종료 시각

    @Column(name = "last_watched_at", nullable = false)
    private LocalDateTime lastWatchedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastWatchedAt = LocalDateTime.now();
    }

    @Builder
    public VideoWatchLog(User user, CourseVideo video) {
        this.user  = user;
        this.video = video;
    }

    public void startSession() {
        this.sessionStartedAt = LocalDateTime.now();
    }

    public void endSession(int watchedSec) {
        this.sessionEndedAt = LocalDateTime.now();
        this.watchedSec     = watchedSec;
        if (watchedSec >= video.getDurationSec()) {
            this.completed = true;
        }
    }
}
