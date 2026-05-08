package com.hr.backend.admin.dto;

import com.hr.backend.domain.course.entity.CourseRound;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CourseRoundResponse {

    private final Long roundId;
    private final Long courseId;
    private final String courseTitle;
    private final int roundNo;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean open;         // 현재 수강 가능 여부
    private final LocalDateTime createdAt;

    public CourseRoundResponse(CourseRound round) {
        this.roundId     = round.getRoundId();
        this.courseId    = round.getCourse().getCourseId();
        this.courseTitle = round.getCourse().getTitle();
        this.roundNo     = round.getRoundNo();
        this.startDate   = round.getStartDate();
        this.endDate     = round.getEndDate();
        this.open        = round.isOpen();
        this.createdAt   = round.getCreatedAt();
    }
}
