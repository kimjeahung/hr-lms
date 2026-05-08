package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    // 수강 일정 관리 (차수 마감일 변경)
    @PutMapping("/schedule/{roundId}")
    public ResponseEntity<CourseRound> manageEnrollmentSchedule(
            @PathVariable Long roundId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(enrollmentService.updateRoundSchedule(roundId, endDate));
    }

    // 수강 상태 변경 (NOT_STARTED, IN_PROGRESS, DONE)
    @PutMapping("/{enrollmentId}/status")
    public ResponseEntity<EnrollmentResponse> changeEnrollmentStatus(
            @PathVariable Long enrollmentId,
            @RequestParam String status) {
        return ResponseEntity.ok(enrollmentService.changeEnrollmentStatus(enrollmentId, status));
    }

    // 수강 통계 조회
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getEnrollmentStatistics() {
        return ResponseEntity.ok(enrollmentService.getEnrollmentStatistics());
    }
}
