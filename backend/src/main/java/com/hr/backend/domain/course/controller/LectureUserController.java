package com.hr.backend.domain.course.controller;

import com.hr.backend.domain.course.dto.LectureWithProgressResponse;
import com.hr.backend.domain.course.service.CourseUserService;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/courses/{courseId}/lectures")
@RequiredArgsConstructor
public class LectureUserController {
    
    private final CourseUserService courseUserService;
    private final UserRepository userRepository;

    /**
     * 강의 목록 조회
     * 완료 여부 및 시청률 표시
     */
    @GetMapping
    public ResponseEntity<List<LectureWithProgressResponse>> getLectureList(@PathVariable Long courseId) {
        Long userId = getLoginUserId();
        // 강좌 상세에서 강의 정보 추출
        var courseDetail = courseUserService.getCourseDetail(userId, courseId);
        return ResponseEntity.ok(courseDetail.getLectures());
    }

    private Long getLoginUserId() {
        String employeeNo = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."))
                .getUserId();
    }
}