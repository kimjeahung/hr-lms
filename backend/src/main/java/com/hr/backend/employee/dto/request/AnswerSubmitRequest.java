package com.hr.backend.employee.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter @Setter
public class AnswerSubmitRequest {
    @NotEmpty
    private Map<Long, Long> answers; // questionId -> choiceId
}
