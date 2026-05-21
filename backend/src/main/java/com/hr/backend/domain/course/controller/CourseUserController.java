package com.hr.backend.domain.course.controller;

import com.hr.backend.domain.course.dto.CourseDetailResponse;
import com.hr.backend.domain.course.dto.CourseListItemResponse;
import com.hr.backend.domain.course.service.CourseUserService;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/courses")
@RequiredArgsConstructor
public class CourseUserController {
    
    private final CourseUserService courseUserService;
    private final UserRepository userRepository;

    /**
     * 강좌 목록 조회 (사용자용)
     * 신청 가능한 강좌 + 차수별 신청 상태 표시
     */
    @GetMapping
    public ResponseEntity<List<CourseListItemResponse>> getCourseList() {
        Long userId = resolveLoginUserIdOrNull();
        return ResponseEntity.ok(courseUserService.getCourseList(userId));
    }

    /**
     * 강좌 상세 조회
     * 강의/퀴즈/시험/설문 상태 포함
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(@PathVariable Long courseId) {
        Long userId = getLoginUserId();
        return ResponseEntity.ok(courseUserService.getCourseDetail(userId, courseId));
    }

    private Long getLoginUserId() {
        String employeeNo = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."))
                .getUserId();
    }

    private Long resolveLoginUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof String employeeNo) || "anonymousUser".equals(employeeNo)) {
            return null;
        }

        return userRepository.findByEmployeeNo(employeeNo)
                .map(user -> user.getUserId())
                .orElse(null);
    }
}