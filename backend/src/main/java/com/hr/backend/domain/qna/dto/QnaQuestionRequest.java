package com.hr.backend.domain.qna.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaQuestionRequest {
    private Long courseId;
    private String title;
    private String content;
}