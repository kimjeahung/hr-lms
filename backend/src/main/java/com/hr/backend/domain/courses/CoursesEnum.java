package com.hr.backend.domain.courses;

public enum CoursesEnum {
    SAFETY("안전교육"),
    SKILL("직무교육"),
    COMPULSORY_EDUCATION("법정의무교육");

    private final String displayName;

    CoursesEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
