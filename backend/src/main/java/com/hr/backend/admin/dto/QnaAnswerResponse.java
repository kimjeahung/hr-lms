package com.hr.backend.admin.dto;

import com.hr.backend.domain.qna.entity.QnaAnswer;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QnaAnswerResponse {

    private final Long          answerId;
    private final Long          questionId;
    private final Long          authorId;
    private final String        authorName;
    private final String        content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public QnaAnswerResponse(QnaAnswer a) {
        this.answerId   = a.getAnswerId();
        this.questionId = a.getQuestion().getQuestionId();
        this.authorId   = a.getAuthor().getUserId();
        this.authorName = a.getAuthor().getName();
        this.content    = a.getContent();
        this.createdAt  = a.getCreatedAt();
        this.updatedAt  = a.getUpdatedAt();
    }
}
