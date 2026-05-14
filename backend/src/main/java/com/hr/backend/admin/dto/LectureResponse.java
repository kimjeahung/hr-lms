package com.hr.backend.admin.dto;

import com.hr.backend.domain.course.entity.Lecture;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LectureResponse {
    private final Long          lectureId;
    private final Long          courseId;
    private final String        title;
    private final String        description;
    private final int           sortOrder;
    private final LocalDateTime createdAt;

    public LectureResponse(Lecture l) {
        this.lectureId   = l.getLectureId();
        this.courseId    = l.getCourse().getCourseId();
        this.title       = l.getTitle();
        this.description = l.getDescription();
        this.sortOrder   = l.getSortOrder();
        this.createdAt   = l.getCreatedAt();
    }
}
