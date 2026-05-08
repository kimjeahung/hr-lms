package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // 전체 이수 현황
    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAll() {
        return ResponseEntity.ok(enrollmentService.getAll());
    }

    // 직원별 이수 현황
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getByUser(userId));
    }

    // 강의별 수강자 현황
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getByCourse(courseId));
    }

    // 수강 등록 (차수 기반)
    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(
            @RequestParam Long userId, @RequestParam Long roundId) {
        return ResponseEntity.ok(enrollmentService.enroll(userId, roundId));
    }

    // 진행률 업데이트
    @PatchMapping("/{enrollmentId}/progress")
    public ResponseEntity<EnrollmentResponse> updateProgress(
            @PathVariable Long enrollmentId, @RequestParam int progress) {
        return ResponseEntity.ok(enrollmentService.updateProgress(enrollmentId, progress));
    }
}
