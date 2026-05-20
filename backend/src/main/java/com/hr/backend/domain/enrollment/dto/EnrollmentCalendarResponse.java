package com.hr.backend.domain.enrollment.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class EnrollmentCalendarResponse {
    private Long roundId;
    private Long courseId;
    private String courseTitle;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;

    // 내 수강 정보 (없으면 null)
    private Long enrollmentId;
    private String myStatus; // NOT_STARTED, IN_PROGRESS, DONE, NONE
    private Integer myProgress; // 0~100, 미수강시 null
}