package com.hr.backend.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "사번은 필수입니다")
    @Size(max = 20, message = "사번은 20자 이하여야 합니다")
    @Pattern(regexp = "^[A-Za-z0-9\\-_]+$", message = "사번은 영문·숫자·하이픈·언더바만 허용합니다")
    private String employeeNo;      // 사번 (A-NNNNN / B-NNNNN)

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    private String email;

    @NotNull(message = "부서 ID는 필수입니다")
    private Integer departmentId;   // 부서 ID

    @NotBlank(message = "직급은 필수입니다")
    @Size(max = 50, message = "직급은 50자 이하여야 합니다")
    private String position;        // 직급

    @Min(value = 0, message = "직원 유형은 0(사무직) 또는 1(현장직)이어야 합니다")
    @Max(value = 1, message = "직원 유형은 0(사무직) 또는 1(현장직)이어야 합니다")
    private int empType;            // 0=사무직, 1=현장직

    @Pattern(regexp = "^$|^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다 (예: 010-1234-5678)")
    private String phone;

    @NotNull(message = "입사일은 필수입니다")
    @PastOrPresent(message = "입사일은 오늘 이전이어야 합니다")
    private LocalDate hireDate;     // 입사일

    @Pattern(regexp = "^(ROLE_USER|ROLE_ADMIN)$", message = "역할은 ROLE_USER 또는 ROLE_ADMIN이어야 합니다")
    private String role;            // ROLE_USER / ROLE_ADMIN
}
