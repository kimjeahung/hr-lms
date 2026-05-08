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
    private final int           progress;
    private final String        status;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;

    public EnrollmentResponse(Enrollment e) {
        this.enrollmentId = e.getEnrollmentId();
        this.userId       = e.getUser().getId();
        this.userName     = e.getUser().getName();
        this.department   = e.getUser().getDepartment();
        this.courseId     = e.getCourse().getCourseId();
        this.courseTitle  = e.getCourse().getTitle();
        this.progress     = e.getProgress();
        this.status       = e.getStatus().name();
        this.startedAt    = e.getStartedAt();
        this.completedAt  = e.getCompletedAt();
    }
}
