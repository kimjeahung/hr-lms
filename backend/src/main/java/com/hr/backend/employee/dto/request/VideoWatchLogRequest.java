package com.hr.backend.employee.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoWatchLogRequest {
    @NotNull(message = "시청 시간은 필수입니다.")
    @Min(value = 0, message = "시청 시간은 0초 이상이어야 합니다.")
    private Integer watchedSec;
}