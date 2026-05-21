package com.hr.backend.domain.enrollment.dto;

import com.hr.backend.domain.enrollment.entity.EnrollmentFeedback;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FeedbackResponse {

    private Long   feedbackId;
    private Long   enrollmentId;
    private int    rating;
    private String comment;
    private LocalDateTime createdAt;

    public FeedbackResponse(EnrollmentFeedback f) {
        this.feedbackId    = f.getFeedbackId();
        this.enrollmentId  = f.getEnrollment().getEnrollmentId();
        this.rating        = f.getRating();
        this.comment       = f.getComment();
        this.createdAt     = f.getCreatedAt();
    }
}
