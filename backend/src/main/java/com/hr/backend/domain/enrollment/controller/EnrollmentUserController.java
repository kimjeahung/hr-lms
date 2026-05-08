package com.hr.backend.domain.enrollment.controller;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/enrollments")
@RequiredArgsConstructor
public class EnrollmentUserController {

    private final EnrollmentService enrollmentService;

    // 수강 신청 - 신청 즉시 IN_PROGRESS
    @PostMapping
    public ResponseEntity<EnrollmentResponse> applyEnrollment(
            @RequestParam Long userId,
            @RequestParam Long roundId) {
        return ResponseEntity.ok(enrollmentService.applyEnrollment(userId, roundId));
    }

    // 본인 수강 이력 조회
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> enrollmentHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getByUser(userId));
    }

    // 본인 전체 수강 내역 조회
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getALLEnrollmentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getByUser(userId));
    }

    // 본인 수강중인 교육 조회
    @GetMapping("/ongoing/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getOngoingEnrollments(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getOngoingEnrollments(userId));
    }

    // 수강 상세 조회
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentDetails(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(enrollmentId));
    }

    // 수강 진행 관리 (진행률 업데이트)
    @PutMapping("/{enrollmentId}/progress")
    public ResponseEntity<EnrollmentResponse> updateProgress(
            @PathVariable Long enrollmentId,
            @RequestParam int progress) {
        return ResponseEntity.ok(enrollmentService.updateProgress(enrollmentId, progress));
    }

    // 수강 완료 처리
    @PutMapping("/{enrollmentId}/complete")
    public ResponseEntity<EnrollmentResponse> completeEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.completeEnrollment(enrollmentId));
    }

    // 수강 피드백 제출 // 실제로는 피드백 DTO를 만들어서 받는 것이 좋지만, 간단히 String으로 받도록 했습니다. 추후 변경
    @PostMapping("/{enrollmentId}/feedback")
    public ResponseEntity<String> submitEnrollmentFeedback(
            @PathVariable Long enrollmentId,
            @RequestParam String feedback) {
        return ResponseEntity.ok("피드백이 제출되었습니다.");
    }

    // 수강 알림 설정// 실제로는 알림 설정 DTO를 만들어서 받는 것이 좋지만, 간단히 boolean으로 받도록 했습니다. 추후 변경
    @PostMapping("/{enrollmentId}/notifications")
    public ResponseEntity<String> setEnrollmentNotifications(
            @PathVariable Long enrollmentId,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok("알림 설정이 변경되었습니다: " + enabled);
    }

    // 수강 캘린더(수강신청스케줄)

}
