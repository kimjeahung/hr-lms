package com.hr.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "employee_no", nullable = false, unique = true, length = 20)
    private String employeeNo;          // 사번 (A-NNNNN / B-NNNNN)

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false, length = 50)
    private String position;            // 직급

    @Column(name = "emp_type", nullable = false)
    private int empType = 0;            // 0=사무직, 1=현장직

    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER";  // ROLE_USER / ROLE_ADMIN

    @Column(length = 20)
    private String phone;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "password_changed", nullable = false)
    private boolean passwordChanged = false;  // 초기 비밀번호(사번) 변경 여부

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public User(String employeeNo, String name, String email, String rawPassword,
                Department department, String position, int empType,
                String role, String phone, LocalDate hireDate,
                PasswordEncoder encoder) {
        this.employeeNo  = employeeNo;
        this.name        = name;
        this.email       = email;
        this.password    = encoder.encode(rawPassword);
        this.department  = department;
        this.position    = position;
        this.empType     = empType;
        this.role        = (role != null) ? role : "ROLE_USER";
        this.phone       = phone;
        this.hireDate    = hireDate;
        this.isActive    = true;
    }

    public void changePassword(String newRawPassword, PasswordEncoder encoder) {
        this.password        = encoder.encode(newRawPassword);
        this.passwordChanged = true;
    }

    public void updateInfo(String name, String email, Department department,
                           String position, int empType, String phone, String role) {
        this.name       = name;
        this.email      = email;
        this.department = department;
        this.position   = position;
        this.empType    = empType;
        this.phone      = phone;
        this.role       = role;
    }

    /** Soft delete */
    public void deactivate() {
        this.isActive = false;
    }

    // 기존 코드 호환용 getter
    public Long getId() {
        return this.userId;
    }
}
