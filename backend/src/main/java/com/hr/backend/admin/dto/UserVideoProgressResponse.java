package com.hr.backend.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 관리자용 직원별 영상/강의 진도 응답 DTO.
 */
@Getter
@Builder
public class UserVideoProgressResponse {

    private Long   userId;
    private String employeeNo;
    private String userName;
    private String departmentName;

    /** 수강 정보 */
    private Long   enrollmentId;
    private String courseTitle;
    private int    enrollmentProgress;   // 0~100

    /** 강의별 진도 목록 */
    private List<LectureProgressDetail> lectures;
    private int totalLectures;
    private int completedLectures;

    @Getter
    @Builder
    public static class LectureProgressDetail {
        private Long    lectureId;
        private String  lectureTitle;
        private boolean completed;
        private int     totalVideos;
        private int     completedVideos;
    }
}
