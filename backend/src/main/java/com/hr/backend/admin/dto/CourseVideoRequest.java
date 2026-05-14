package com.hr.backend.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourseVideoRequest {
    private String title;
    private String videoUrl;
    private int    durationSec;
    private int    sortOrder;
}
