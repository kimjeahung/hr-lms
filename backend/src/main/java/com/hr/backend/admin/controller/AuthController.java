package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.ChangePasswordRequest;
import com.hr.backend.admin.dto.LoginRequest;
import com.hr.backend.admin.dto.LoginResponse;
import com.hr.backend.domain.user.service.AuthService;
import com.hr.backend.domain.user.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordService passwordService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal String employeeNo,
            @RequestBody ChangePasswordRequest req) {
        passwordService.changePassword(employeeNo, req);
        return ResponseEntity.noContent().build();
    }
}
