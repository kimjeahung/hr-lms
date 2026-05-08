package com.hr.backend.admin.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CourseRequest {
    private String    title;
    private String    description;
    private String    category;
    private int       targetRole;
    private Integer   durationMin;
    private LocalDate deadline;
}
