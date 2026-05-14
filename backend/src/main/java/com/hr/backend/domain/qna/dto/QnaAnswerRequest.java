package com.hr.backend.domain.qna.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaAnswerRequest {
    private Long questionId;
    private String content;
}