package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.enrollment.service.EnrollmentService;
import com.hr.backend.domain.enrollments.entity.Enrollments;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
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

    // 수강 등록
    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(
            @RequestParam Long userId, @RequestParam Long courseId) {
        return ResponseEntity.ok(enrollmentService.enroll(userId, courseId));
    }

    // 진행률 업데이트
    @PatchMapping("/{enrollmentId}/progress")
    public ResponseEntity<EnrollmentResponse> updateProgress(
            @PathVariable Long enrollmentId, @RequestParam int progress) {
        return ResponseEntity.ok(enrollmentService.updateProgress(enrollmentId, progress));
    }

    //

     // 수강 신청
    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollments(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        return ResponseEntity.ok(enrollmentService.registerCourse(userId, courseId));
    }

    // 수강 이력 조회
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> enrollmentHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentHistory(userId));
    }

    // 수강 진행 관리
    @PutMapping("/{enrollmentId}/progress")
    public ResponseEntity<EnrollmentResponse> manageCourseSessions(
            @PathVariable Long enrollmentId,
            @RequestParam int progress) {
        return ResponseEntity.ok(enrollmentService.manageCourseSessions(enrollmentId, progress));
    }

    // 수강 상세조회
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentDetails(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(enrollmentId));
    }

    // 수강 일정 관리
    @PutMapping("/schedule/{courseId}")
    public ResponseEntity<Courses> manageEnrollmentSchedule(
            @PathVariable Long courseId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date deadline) {
        return ResponseEntity.ok(enrollmentService.manageEnrollmentSchedule(courseId, deadline));
    }

    // 수강 완료 처리
    @PutMapping("/{enrollmentId}/complete")
    public ResponseEntity<EnrollmentResponse> completeEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.completeEnrollment(enrollmentId));
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

    // 수강 피드백 제출
    @PostMapping("/{enrollmentId}/feedback")
    public ResponseEntity<String> submitEnrollmentFeedback(
            @PathVariable Long enrollmentId,
            @RequestParam String feedback) {
        return ResponseEntity.ok("피드백이 제출되었습니다.");
    }

    // 수강 알림 설정
    @PostMapping("/{enrollmentId}/notifications")
    public ResponseEntity<String> setEnrollmentNotifications(
            @PathVariable Long enrollmentId,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok("알림 설정이 변경되었습니다: " + enabled);
    }

    // 수강중인 교육 조회
    @GetMapping("/ongoing/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getOngoingEnrollments(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getOngoingEnrollments(userId));
    }

    //특정 유저 수강 내역 조회(본인수강 내역 조회)
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getALLEnrollmentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getALLEnrollmentsByUser(userId));
    }
}
