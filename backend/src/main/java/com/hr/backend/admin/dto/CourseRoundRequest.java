package com.hr.backend.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CourseRoundRequest {

    private int roundNo;          // 차수 번호
    private LocalDate startDate;  // 수강 시작일
    private LocalDate endDate;    // 수강 마감일
}
