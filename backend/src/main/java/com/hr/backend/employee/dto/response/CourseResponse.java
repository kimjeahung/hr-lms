package com.hr.backend.employee.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class CourseResponse {
    private Long courseId;
    private String title;
    private String description;
    private String category;
    private String targetRole;
    private Integer durationMin;
    private String thumbnailURL;
    private LocalDate deadline;
    private Boolean isActive;
    private Boolean isEnrolled; // 현재 사용자가 수강 중인지 여부
    private Integer enrollmentProgress; // 수강 중이라면 진행률
    private String enrollmentStatus; // 수강 중이라면 상태

    // 강의 목록 조회 시 사용
    @Getter
    @Setter
    @Builder
    public static class CourseListItem {
        private Long courseId;
        private String title;
        private String category;
        private Integer durationMin;
        private String thumbnailURL;
        private LocalDate deadline;
        private Boolean isEnrolled;
        private Integer enrollmentProgress;
    }

    // 강의 상세 조회 시 사용
    @Getter
    @Setter
    @Builder
    public static class CourseDetailResponse {
        private Long courseId;
        private String title;
        private String description;
        private String category;
        private String targetRole;
        private Integer durationMin;
        private String thumbnailURL;
        private LocalDate deadline;
        private Boolean isActive;
        private EnrollmentStatusDto myEnrollmentStatus; // 내 수강 상태
        private List<CourseVideoResponse> videos; // 강의 영상 목록
    }

    // 강의 영상 목록 (CourseDetailResponse 내부에 포함)
    @Getter
    @Setter
    @Builder
    public static class CourseVideoResponse {
        private Long videoId;
        private String title;
        private String videoURL;
        private Integer durationSec;
        private Integer sortOrder;
    }

    // 내 수강 상태 (CourseDetailResponse 내부에 포함)
    @Getter
    @Setter
    @Builder
    public static class EnrollmentStatusDto {
        private Long enrollmentId;
        private Integer progress;
        private String status;
        private Long userId;
    }
}