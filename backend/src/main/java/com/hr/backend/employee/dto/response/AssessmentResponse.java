package com.hr.backend.employee.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter @Builder
public class AssessmentResponse {
    private Long id;
    private String type;
    private String title;
    private Integer passScore;
    private List<QuestionItem> questions;

    @Getter @Builder
    public static class QuestionItem {
        private Long questionId;
        private String questionText;
        private Integer score;
        private Integer sortOrder;
        private List<ChoiceItem> choices;
    }

    @Getter @Builder
    public static class ChoiceItem {
        private Long choiceId;
        private String choiceText;
        private Integer sortOrder;
    }
}
