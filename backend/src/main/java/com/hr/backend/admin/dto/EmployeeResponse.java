package com.hr.backend.admin.dto;

import com.hr.backend.domain.user.entity.User;
import lombok.Getter;

@Getter
public class EmployeeResponse {
    private final Long    id;
    private final String  employeeNo;
    private final String  name;
    private final String  department;
    private final String  role;
    private final boolean passwordChanged;

    public EmployeeResponse(User user) {
        this.id              = user.getId();
        this.employeeNo      = user.getEmployeeNo();
        this.name            = user.getName();
        this.department      = user.getDepartment();
        this.role            = user.getRole().name();
        this.passwordChanged = user.isPasswordChanged();
    }
}
