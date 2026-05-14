package com.hr.backend.domain.qna.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class QnaQuestionResponse {
    private Long questionId;
    private Long courseId;
    private Long userId;
    private String title;
    private String content;
    private boolean isResolved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}