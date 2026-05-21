package com.hr.backend.admin.dto;

import com.hr.backend.domain.qna.entity.QnaQuestion;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class QnaQuestionResponse {

    private final Long               questionId;
    private final Long               courseId;
    private final String             courseTitle;
    private final Long               userId;
    private final String             userName;
    private final String             title;
    private final String             content;
    private final boolean            resolved;
    private final int                answerCount;
    private final List<QnaAnswerResponse> answers;
    private final LocalDateTime      createdAt;
    private final LocalDateTime      updatedAt;

    public QnaQuestionResponse(QnaQuestion q) {
        this.questionId  = q.getQuestionId();
        this.courseId    = q.getCourse().getCourseId();
        this.courseTitle = q.getCourse().getTitle();
        this.userId      = q.getUser().getUserId();
        this.userName    = q.getUser().getName();
        this.title       = q.getTitle();
        this.content     = q.getContent();
        this.resolved    = q.isResolved();
        this.answerCount = q.getAnswers().size();
        this.answers     = q.getAnswers().stream().map(QnaAnswerResponse::new).toList();
        this.createdAt   = q.getCreatedAt();
        this.updatedAt   = q.getUpdatedAt();
    }
}
