package com.hr.backend.domain.enrollment.entity;

import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 이수증 - 최종 시험 통과 시 발급
 * round_id 참조로 어느 차수에서 이수했는지 추적
 */
@Entity
@Table(name = "certificates",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "round_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private CourseRound round;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "file_url", length = 500)
    private String fileUrl;   // 이수증 PDF 경로

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }

    @Builder
    public Certificate(User user, CourseRound round, String fileUrl) {
        this.user    = user;
        this.round   = round;
        this.fileUrl = fileUrl;
    }
}
