package com.hr.backend.domain.enrollment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnrollmentScheduleResponse {
    private Long enrollmentId;
    private Long userId;
    private String userName;
    private Long roundId;
    private Integer roundNo;
    private Long courseId;
    private String courseTitle;
    private String startDate;
    private String endDate;
    private Integer durationMin;
    private Integer trainingHours;
    private String status;
    private Integer progress;
}
