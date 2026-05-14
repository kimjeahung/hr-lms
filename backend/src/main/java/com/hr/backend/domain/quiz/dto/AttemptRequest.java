package com.hr.backend.domain.quiz.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 퀴즈/시험 응시 요청 DTO
 * answers: { questionId → choiceId }
 */
@Getter
@NoArgsConstructor
public class AttemptRequest {

    /** key = questionId, value = 선택한 choiceId */
    private Map<Long, Long> answers;
}
