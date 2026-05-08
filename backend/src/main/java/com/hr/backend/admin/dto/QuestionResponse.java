package com.hr.backend.admin.dto;

import com.hr.backend.domain.quiz.entity.Question;
import lombok.Getter;

import java.util.List;

@Getter
public class QuestionResponse {

    private final Long questionId;
    private final String questionText;
    private final int score;
    private final int sortOrder;
    private final List<ChoiceResponse> choices;

    public QuestionResponse(Question question) {
        this.questionId   = question.getQuestionId();
        this.questionText = question.getQuestionText();
        this.score        = question.getScore();
        this.sortOrder    = question.getSortOrder();
        this.choices      = question.getChoices().stream()
                .map(ChoiceResponse::new)
                .toList();
    }
}
