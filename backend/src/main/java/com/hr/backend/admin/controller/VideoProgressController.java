package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.UserVideoProgressResponse;
import com.hr.backend.domain.course.service.VideoProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 관리자용 직원별 영상 학습 진도 조회 API.
 *
 * GET /api/admin/progress/enrollments/{enrollmentId}         — 수강 건 상세 진도
 * GET /api/admin/progress/users/{userId}                     — 직원 전체 수강 진도 목록
 * GET /api/admin/progress/enrollments/{enrollmentId}/summary — 진도 요약
 */
@RestController
@RequestMapping("/api/admin/progress")
@RequiredArgsConstructor
public class VideoProgressController {

    private final VideoProgressService videoProgressService;

    @GetMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<UserVideoProgressResponse> getEnrollmentProgress(
            @PathVariable Long enrollmentId) {
        return ResponseEntity.ok(videoProgressService.getEnrollmentProgress(enrollmentId));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserVideoProgressResponse>> getUserProgress(
            @PathVariable Long userId) {
        return ResponseEntity.ok(videoProgressService.getUserProgress(userId));
    }

    @GetMapping("/enrollments/{enrollmentId}/summary")
    public ResponseEntity<Map<String, Object>> getProgressSummary(
            @PathVariable Long enrollmentId) {
        return ResponseEntity.ok(videoProgressService.getProgressSummary(enrollmentId));
    }
}
