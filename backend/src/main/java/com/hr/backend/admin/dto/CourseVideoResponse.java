package com.hr.backend.admin.dto;

import com.hr.backend.domain.course.entity.CourseVideo;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CourseVideoResponse {
    private final Long          videoId;
    private final Long          lectureId;
    private final String        title;
    private final String        videoUrl;
    private final int           durationSec;
    private final int           sortOrder;
    private final LocalDateTime createdAt;

    public CourseVideoResponse(CourseVideo v) {
        this.videoId     = v.getVideoId();
        this.lectureId   = v.getLecture().getLectureId();
        this.title       = v.getTitle();
        this.videoUrl    = v.getVideoUrl();
        this.durationSec = v.getDurationSec();
        this.sortOrder   = v.getSortOrder();
        this.createdAt   = v.getCreatedAt();
    }
}
