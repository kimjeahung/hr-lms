package com.hr.backend.domain.user.service;

import com.hr.backend.admin.dto.ChangePasswordRequest;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(String employeeNo, ChangePasswordRequest req) {
        User user = userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        if (req.getNewPassword() == null || req.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("새 비밀번호는 8자 이상이어야 합니다.");
        }

        // 비밀번호 복잡도: 대문자, 소문자, 숫자, 특수문자 각 1개 이상 포함
        if (!req.getNewPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':,.<>?]).{8,}$")) {
            throw new IllegalArgumentException("비밀번호는 대문자, 소문자, 숫자, 특수문자를 각 1개 이상 포함해야 합니다.");
        }

        user.changePassword(req.getNewPassword(), passwordEncoder);
    }
}
