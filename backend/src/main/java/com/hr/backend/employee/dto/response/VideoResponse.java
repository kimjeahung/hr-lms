package com.hr.backend.employee.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class VideoResponse {
    private Long videoId;
    private String title;
    private String videoURL;
    private Integer durationSec;
    private Integer sortOrder;
    private Integer watchedSec; // 내 시청 로그
    private Boolean isCompleted; // 내 시청 완료 여부

    @Getter
    @Setter
    @Builder
    public static class VideoListResponse {
        private Long courseId;
        private String courseTitle;
        private List<VideoResponse> videos;
    }
}