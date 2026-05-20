package com.hr.backend.domain.course.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 차수 - 강좌(Course)를 특정 기간에 운영하는 단위
 * 기한 내 이수증 발급 = 출석
 */
@Entity
@Table(name = "course_rounds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_id")
    private Long roundId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "round_no", nullable = false)
    private int roundNo;           // 차수 번호 (1, 2, 3...)

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;   // 수강 시작일

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;     // 수강 마감일

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public CourseRound(Course course, int roundNo, LocalDate startDate, LocalDate endDate) {
        this.course    = course;
        this.roundNo   = roundNo;
        this.startDate = startDate;
        this.endDate   = endDate;
    }

    public boolean isOpen() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public void update(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate   = endDate;
    }
}
