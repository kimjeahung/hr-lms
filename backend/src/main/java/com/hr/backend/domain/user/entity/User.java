package com.hr.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_no", nullable = false, unique = true, length = 20)
    private String employeeNo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String department;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Role role;

    @Column(name = "password_changed", nullable = false)
    private boolean passwordChanged = false;

    public enum Role {
        ADMIN,   // 0
        FIELD,   // 1
        OFFICE   // 2
    }

    @Builder
    public User(String employeeNo, String rawPassword,
                String name, String department, Role role,
                PasswordEncoder encoder) {
        this.employeeNo = employeeNo;
        this.password   = encoder.encode(rawPassword);
        this.name       = name;
        this.department = department;
        this.role       = role;
    }

    public void changePassword(String newRawPassword, PasswordEncoder encoder) {
        this.password        = encoder.encode(newRawPassword);
        this.passwordChanged = true;
    }

    public void updateInfo(String name, String department, Role role) {
        this.name       = name;
        this.department = department;
        this.role       = role;
    }
}
