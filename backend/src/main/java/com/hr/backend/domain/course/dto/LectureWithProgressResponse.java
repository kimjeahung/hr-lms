package com.hr.backend.domain.course.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LectureWithProgressResponse {
    private Long lectureId;
    private String title;
    private String description;
    private Integer sortOrder;
    
    // 시청 진도
    private Integer videoCount;
    private Integer completedVideoCount;
    private Double watchPercentage; // 0.0 ~ 100.0
    
    // 강의 완료 여부
    private Boolean isCompleted;
}