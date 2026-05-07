package com.hr.backend.admin.dto;

import com.hr.backend.domain.user.entity.User;
import lombok.Getter;

@Getter
public class EmployeeRequest {
    private String employeeNo;
    private String name;
    private String department;
    private int    role;

    public User.Role toRole() {
        return User.Role.values()[role];
    }
}
