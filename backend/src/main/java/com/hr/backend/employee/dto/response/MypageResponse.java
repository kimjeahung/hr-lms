package com.hr.backend.employee.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class MypageResponse {
    private Long userId;
    private String employeeNo;
    private String name;
    private String email;
    private String departmentName;
    private String position;
    private String empType;
    private LocalDate hireDate;
    private Double overallCompletionRate; // 전체 이수율
    private long completedCoursesCount; // 수료 강의 수
    private List<MypageCertificateItem> certificates; // 이수증 목록

    @Getter
    @Setter
    @Builder
    public static class MypageCertificateItem {
        private Long certificateId;
        private String courseTitle;
        private String fileURL;
    }
}