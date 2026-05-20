package com.hr.backend.employee.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class AttemptResponse {
    private Long attemptId;
    private String type;
    private Long targetId;
    private Integer score;
    private Boolean passed;
    private LocalDateTime attemptedAt;
}
