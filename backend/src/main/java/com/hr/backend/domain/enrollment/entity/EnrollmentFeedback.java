package com.hr.backend.domain.enrollment.entity;

import com.hr.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 수강 완료 후 사용자가 제출하는 교육 피드백.
 * enrollment 당 최대 1건 (unique constraint).
 */
@Entity
@Table(name = "enrollment_feedbacks",
       uniqueConstraints = @UniqueConstraint(columnNames = "enrollment_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 별점 1~5 */
    @Column(nullable = false)
    private int rating;

    /** 자유 텍스트 코멘트 */
    @Column(length = 2000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public EnrollmentFeedback(Enrollment enrollment, User user, int rating, String comment) {
        this.enrollment = enrollment;
        this.user       = user;
        this.rating     = rating;
        this.comment    = comment;
    }

    /** 기존 피드백 수정 */
    public void update(int rating, String comment) {
        this.rating  = rating;
        this.comment = comment;
    }
}
