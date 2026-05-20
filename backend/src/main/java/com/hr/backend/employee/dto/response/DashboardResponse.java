package com.hr.backend.employee.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class DashboardResponse {
    private String userName;
    private long currentCoursesCount; // 수강 중 교육 개수
    private long completedCoursesCount; // 완료 교육 개수
    private Double overallCompletionRate; // 전체 이수율
    private List<DashboardCourseItem> inProgressCourses; // 수강 중인 교육 목록
    private List<DashboardCourseItem> mandatoryCoursesStatus; // 법정의무교육 진행 현황 (예시로 일반 코스 아이템 재활용)
    private List<DashboardNoticeItem> recentNotices; // 최근 공지사항 목록
    private long unreadNotificationsCount; // 안 읽은 알림 개수

    @Getter
    @Setter
    @Builder
    public static class DashboardCourseItem {
        private Long courseId;
        private String title;
        private String thumbnailURL;
        private Integer progress; // 이수율
        private String status; // EnrollmentStatus
        private LocalDate deadline;
    }

    @Getter
    @Setter
    @Builder
    public static class DashboardNoticeItem {
        private Long noticeId;
        private String title;
        private String contentPreview; // 첫 몇 줄만
        private Boolean isPinned;
    }
}