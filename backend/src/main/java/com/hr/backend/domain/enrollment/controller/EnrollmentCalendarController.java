package com.hr.backend.domain.enrollment.controller;

import com.hr.backend.domain.enrollment.dto.EnrollmentCalendarResponse;
import com.hr.backend.domain.enrollment.service.EnrollmentCalendarService;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/calendar")
@RequiredArgsConstructor
public class EnrollmentCalendarController {
    
    private final EnrollmentCalendarService calendarService;
    private final UserRepository userRepository;


    /**
     * 전체 강의 일정 + 내 수강상태 포함
     * 하위호환을 위해 /api/user/calendar 와 /api/user/calendar/all 모두 지원
     */
    @GetMapping({"", "/all"})
    public ResponseEntity<List<EnrollmentCalendarResponse>> getAllRoundsWithMyStatus() {
        Long userId = getLoginUserId();
        return ResponseEntity.ok(calendarService.getAllRoundsWithMyStatus(userId));
    }

    private Long getLoginUserId() {
        String employeeNo = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."))
                .getUserId();
    }
}