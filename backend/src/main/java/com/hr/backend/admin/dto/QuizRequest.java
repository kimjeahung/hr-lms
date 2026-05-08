package com.hr.backend.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuizRequest {

    private String title;
    private int passScore;
}
