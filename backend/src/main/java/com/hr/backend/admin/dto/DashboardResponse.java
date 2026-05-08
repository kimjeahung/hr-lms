package com.hr.backend.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    private int totalEmployees;
    private int totalCourses;
    private int notCompletedCount;     // 미이수자 수
    private double overallCompletionRate; // 전체 이수율 (%)

    private List<DeptStat>    deptStats;       // 부서별 이수율
    private List<LowProgress> lowProgressList; // 미이수자 상위 (진행률 낮은 순)
    private List<DeadlineAlert> deadlineAlerts; // 마감 임박 강의

    @Getter
    @Builder
    public static class DeptStat {
        private String dept;
        private double completionRate;
    }

    @Getter
    @Builder
    public static class LowProgress {
        private Long   userId;
        private String name;
        private String department;
        private String role;
        private double completionRate;
    }

    @Getter
    @Builder
    public static class DeadlineAlert {
        private Long   courseId;
        private String title;
        private String deadline;
        private int    notCompletedCount;
    }
}
