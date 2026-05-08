package com.hr.backend.admin.dto;

import com.hr.backend.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class EmployeeResponse {

    private final Long userId;
    private final String employeeNo;
    private final String name;
    private final String email;
    private final Integer departmentId;
    private final String departmentName;
    private final String position;
    private final int empType;          // 0=사무직, 1=현장직
    private final String phone;
    private final LocalDate hireDate;
    private final String role;
    private final boolean active;
    private final LocalDateTime createdAt;

    public EmployeeResponse(User user) {
        this.userId         = user.getUserId();
        this.employeeNo     = user.getEmployeeNo();
        this.name           = user.getName();
        this.email          = user.getEmail();
        this.departmentId   = user.getDepartment() != null ? user.getDepartment().getDepartmentId() : null;
        this.departmentName = user.getDepartment() != null ? user.getDepartment().getName() : null;
        this.position       = user.getPosition();
        this.empType        = user.getEmpType();
        this.phone          = user.getPhone();
        this.hireDate       = user.getHireDate();
        this.role           = user.getRole();
        this.active         = user.isActive();
        this.createdAt      = user.getCreatedAt();
    }
}
