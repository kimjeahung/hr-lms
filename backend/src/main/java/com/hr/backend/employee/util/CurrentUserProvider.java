package com.hr.backend.employee.util;

import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {
    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("로그인 사용자 정보를 찾을 수 없습니다.");
        }
        String employeeNo = authentication.getName();
        return userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalStateException("로그인 사용자 정보를 찾을 수 없습니다: " + employeeNo));
    }

    public String getCurrentUserEmpType() {
        return String.valueOf(getCurrentUser().getEmpType());
    }
}
