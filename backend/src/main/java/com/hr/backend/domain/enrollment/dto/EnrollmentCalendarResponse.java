package com.hr.backend.domain.enrollment.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class EnrollmentCalendarResponse {
    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // NOT_STARTED, IN_PROGRESS, DONE
    private Integer progress; // 0~100
}