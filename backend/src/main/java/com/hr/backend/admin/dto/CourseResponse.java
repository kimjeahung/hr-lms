package com.hr.backend.admin.dto;

import com.hr.backend.domain.course.entity.Course;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CourseResponse {
    private final Long          courseId;
    private final String        title;
    private final String        description;
    private final String        category;
    private final int           targetRole;
    private final Integer       durationMin;
    private final LocalDate     deadline;
    private final boolean       active;
    private final LocalDateTime createdAt;

    public CourseResponse(Course c) {
        this.courseId    = c.getCourseId();
        this.title       = c.getTitle();
        this.description = c.getDescription();
        this.category    = c.getCategory();
        this.targetRole  = c.getTargetRole();
        this.durationMin = c.getDurationMin();
        this.deadline    = c.getDeadline();
        this.active      = c.isActive();
        this.createdAt   = c.getCreatedAt();
    }
}
