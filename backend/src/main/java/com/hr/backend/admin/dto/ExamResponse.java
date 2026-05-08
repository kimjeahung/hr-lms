package com.hr.backend.admin.dto;

import com.hr.backend.domain.quiz.entity.Exam;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ExamResponse {

    private final Long examId;
    private final Long courseId;
    private final String courseTitle;
    private final String title;
    private final int passScore;
    private final List<QuestionResponse> questions;
    private final LocalDateTime createdAt;

    public ExamResponse(Exam exam) {
        this.examId      = exam.getExamId();
        this.courseId    = exam.getCourse().getCourseId();
        this.courseTitle = exam.getCourse().getTitle();
        this.title       = exam.getTitle();
        this.passScore   = exam.getPassScore();
        this.questions   = exam.getQuestions().stream()
                .map(QuestionResponse::new)
                .toList();
        this.createdAt   = exam.getCreatedAt();
    }
}
