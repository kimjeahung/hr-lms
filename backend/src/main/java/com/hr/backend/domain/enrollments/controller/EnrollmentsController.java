package com.hr.backend.domain.enrollments.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hr.backend.domain.courses.entity.Courses;
import com.hr.backend.domain.enrollments.entity.Enrollments;
import com.hr.backend.domain.enrollments.service.CourseRegistrationservice;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enrollments")
public class EnrollmentsController {

    private final CourseRegistrationservice courseRegistrationservice;

    // 수강 신청
    @PostMapping
    public ResponseEntity<Enrollments> enrollments(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        return ResponseEntity.ok(courseRegistrationservice.registerCourse(userId, courseId));
    }

    // 수강 이력 조회
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Enrollments>> enrollmentHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(courseRegistrationservice.getEnrollmentHistory(userId));
    }

    // 수강 진행 관리
    @PutMapping("/{enrollmentId}/progress")
    public ResponseEntity<Enrollments> manageCourseSessions(
            @PathVariable Long enrollmentId,
            @RequestParam int progress) {
        return ResponseEntity.ok(courseRegistrationservice.manageCourseSessions(enrollmentId, progress));
    }

    // 수강 상세조회
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<Enrollments> getEnrollmentDetails(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(courseRegistrationservice.getEnrollmentById(enrollmentId));
    }

    // 수강 일정 관리
    @PutMapping("/schedule/{courseId}")
    public ResponseEntity<Courses> manageEnrollmentSchedule(
            @PathVariable Long courseId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date deadline) {
        return ResponseEntity.ok(courseRegistrationservice.manageEnrollmentSchedule(courseId, deadline));
    }

    // 수강 완료 처리
    @PutMapping("/{enrollmentId}/complete")
    public ResponseEntity<Enrollments> completeEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(courseRegistrationservice.completeEnrollment(enrollmentId));
    }

    // 수강 상태 변경 (NOT_STARTED, IN_PROGRESS, DONE)
    @PutMapping("/{enrollmentId}/status")
    public ResponseEntity<Enrollments> changeEnrollmentStatus(
            @PathVariable Long enrollmentId,
            @RequestParam String status) {
        return ResponseEntity.ok(courseRegistrationservice.changeEnrollmentStatus(enrollmentId, status));
    }

    // 수강 통계 조회
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getEnrollmentStatistics() {
        return ResponseEntity.ok(courseRegistrationservice.getEnrollmentStatistics());
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
    public ResponseEntity<List<Enrollments>> getOngoingEnrollments(@PathVariable Long userId) {
        return ResponseEntity.ok(courseRegistrationservice.getOngoingEnrollments(userId));
    }

    //특정 유저 수강 내역 조회(본인수강 내역 조회)
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Enrollments>> getALLEnrollmentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(courseRegistrationservice.getALLEnrollmentsByUser(userId));
    }
}
