package com.hr.backend.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class EmployeeRequest {

    private String employeeNo;      // 사번 (A-NNNNN / B-NNNNN)
    private String name;
    private String email;
    private Integer departmentId;   // 부서 ID
    private String position;        // 직급
    private int empType;            // 0=사무직, 1=현장직
    private String phone;
    private LocalDate hireDate;     // 입사일
    private String role;            // ROLE_USER / ROLE_ADMIN
}
