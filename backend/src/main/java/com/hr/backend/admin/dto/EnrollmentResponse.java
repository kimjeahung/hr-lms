package com.hr.backend.admin.dto;

import com.hr.backend.domain.enrollment.entity.Enrollment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EnrollmentResponse {
    private final Long          enrollmentId;
    private final Long          userId;
    private final String        userName;
    private final String        department;
    private final Long          courseId;
    private final String        courseTitle;
    private final Long          roundId;
    private final int           roundNo;
    private final int           progress;
    private final String        approvalStatus;
    private final String        status;
    private final LocalDateTime enrolledAt;
    private final LocalDateTime completedAt;

    public EnrollmentResponse(Enrollment e) {
        this.enrollmentId   = e.getEnrollmentId();
        this.userId         = e.getUser().getUserId();
        this.userName       = e.getUser().getName();
        this.department     = e.getUser().getDepartment() != null
                              ? e.getUser().getDepartment().getName() : "";
        this.courseId       = e.getRound().getCourse().getCourseId();
        this.courseTitle    = e.getRound().getCourse().getTitle();
        this.roundId        = e.getRound().getRoundId();
        this.roundNo        = e.getRound().getRoundNo();
        this.progress       = e.getProgress();
        this.approvalStatus = e.getApprovalStatus().name();
        this.status         = e.getStatus().name();
        this.enrolledAt     = e.getEnrolledAt();
        this.completedAt    = e.getCompletedAt();
    }
}
