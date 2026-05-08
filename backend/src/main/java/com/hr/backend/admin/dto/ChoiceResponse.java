package com.hr.backend.admin.dto;

import com.hr.backend.domain.quiz.entity.Choice;
import lombok.Getter;

@Getter
public class ChoiceResponse {

    private final Long choiceId;
    private final String choiceText;
    private final boolean correct;
    private final int sortOrder;

    public ChoiceResponse(Choice choice) {
        this.choiceId   = choice.getChoiceId();
        this.choiceText = choice.getChoiceText();
        this.correct    = choice.isCorrect();
        this.sortOrder  = choice.getSortOrder();
    }
}
