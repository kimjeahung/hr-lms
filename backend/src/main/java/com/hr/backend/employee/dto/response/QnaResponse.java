package com.hr.backend.employee.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class QnaResponse {
    private Long questionId;
    private Long courseId;
    private String courseTitle;
    private String title;
    private String content;
    private Boolean resolved;
    private LocalDateTime createdAt;
    private List<AnswerItem> answers;

    @Getter @Builder
    public static class AnswerItem {
        private Long answerId;
        private String authorName;
        private String content;
        private LocalDateTime createdAt;
    }
}
