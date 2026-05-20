package com.hr.backend.admin.dto;

import lombok.Getter;

@Getter
public class CourseRequest {
    private String  title;
    private String  description;
    private String  category;
    private int     targetRole;
    private Integer durationMin;
    private String  thumbnailUrl;
}
