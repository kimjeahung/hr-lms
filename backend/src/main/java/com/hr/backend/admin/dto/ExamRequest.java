package com.hr.backend.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExamRequest {

    private String title;
    private int passScore;
}
