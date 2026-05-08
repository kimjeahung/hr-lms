package com.hr.backend.domain.course.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_videos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long videoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;  // 상위 강의

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;

    @Column(name = "duration_sec", nullable = false)
    private int durationSec = 0;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public CourseVideo(Lecture lecture, String title, String videoUrl,
                       int durationSec, int sortOrder) {
        this.lecture     = lecture;
        this.title       = title;
        this.videoUrl    = videoUrl;
        this.durationSec = durationSec;
        this.sortOrder   = sortOrder;
    }

    public void update(String title, String videoUrl, int durationSec, int sortOrder) {
        this.title       = title;
        this.videoUrl    = videoUrl;
        this.durationSec = durationSec;
        this.sortOrder   = sortOrder;
    }
}
