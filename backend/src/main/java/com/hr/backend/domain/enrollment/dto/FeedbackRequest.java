package com.hr.backend.domain.enrollment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedbackRequest {

    /** 별점 (1~5) */
    private int rating;

    /** 자유 코멘트 (선택) */
    private String comment;
}
