package com.hr.backend.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QnaQuestionRequest {
    @NotNull private Long courseId;
    @NotBlank private String title;
    @NotBlank private String content;
}
