package com.hr.backend.domain.enrollment.enums;

public enum EnrollmentStatus {
    NOT_STARTED("미수강"),
    IN_PROGRESS("수강 중"),
    DONE("수강 완료");

    private final String displayName;

    EnrollmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
