package com.hr.backend.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class QuestionRequest {

    private String questionText;
    private int score;
    private int sortOrder;
    private List<ChoiceRequest> choices;  // 객관식 선택지 (필수)
}
