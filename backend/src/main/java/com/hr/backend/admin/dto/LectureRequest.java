package com.hr.backend.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureRequest {
    private String title;
    private String description;
    private int    sortOrder;
}
