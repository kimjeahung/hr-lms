package com.hr.backend.domain.user.service;

import com.hr.backend.admin.dto.LoginRequest;
import com.hr.backend.admin.dto.LoginResponse;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import com.hr.backend.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmployeeNo(req.getEmployeeNo())
                .orElseThrow(() -> new IllegalArgumentException("사원번호 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("사원번호 또는 비밀번호가 올바르지 않습니다.");
        }

        String role  = user.getRole();
        String token = jwtProvider.generate(user.getEmployeeNo(), role);

        return new LoginResponse(token, user.getName(), role, user.isPasswordChanged());
    }
}
