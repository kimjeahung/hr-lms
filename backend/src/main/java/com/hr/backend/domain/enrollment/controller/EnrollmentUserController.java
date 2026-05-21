package com.hr.backend.domain.enrollment.controller;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.enrollment.dto.EnrollmentScheduleResponse;
import com.hr.backend.domain.enrollment.dto.FeedbackRequest;
import com.hr.backend.domain.enrollment.dto.FeedbackResponse;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.enrollment.service.EnrollmentService;
import com.hr.backend.domain.enrollment.service.FeedbackService;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/user/enrollments")
@RequiredArgsConstructor
public class EnrollmentUserController {

    private final EnrollmentService      enrollmentService;
    private final FeedbackService        feedbackService;
    private final EnrollmentRepository   enrollmentRepository;
    private final UserRepository         userRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 수강 신청 — JWT에서 userId 추출 (타인 명의 신청 방지)
     * [수정] @RequestParam Long userId 제거 → getLoginUserId() 로 대체
     */
    @PostMapping
    public ResponseEntity<EnrollmentResponse> applyEnrollment(@RequestParam Long roundId) {
        Long userId = getLoginUserId();
        return ResponseEntity.ok(enrollmentService.applyEnrollment(userId, roundId));
    }

    // 본인 수강 이력 조회
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> enrollmentHistory(@PathVariable Long userId) {
        if (!getLoginUserId().equals(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(enrollmentService.getHistoryByUser(userId));
    }

    // 본인 전체 수강 내역 조회
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getALLEnrollmentsByUser(@PathVariable Long userId) {
        if (!getLoginUserId().equals(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(enrollmentService.getByUser(userId));
    }

    // 본인 수강중인 교육 조회
    @GetMapping("/ongoing/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getOngoingEnrollments(@PathVariable Long userId) {
        if (!getLoginUserId().equals(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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

    // 수강 피드백 제출 (수강 완료 후에만 가능, 기존 제출 시 수정 처리)
    @PostMapping("/{enrollmentId}/feedback")
    public ResponseEntity<FeedbackResponse> submitEnrollmentFeedback(
            @PathVariable Long enrollmentId,
            @RequestBody FeedbackRequest request) {
        String employeeNo = getLoginEmployeeNo();
        return ResponseEntity.ok(feedbackService.submitFeedback(enrollmentId, employeeNo, request));
    }

    // 피드백 조회
    @GetMapping("/{enrollmentId}/feedback")
    public ResponseEntity<FeedbackResponse> getEnrollmentFeedback(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(feedbackService.getFeedback(enrollmentId));
    }

    // 수강 캘린더 (기본)
    @GetMapping("/{enrollmentId}/schedule")
    public ResponseEntity<String> getEnrollmentSchedule(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok("수강 일정 정보입니다.");
    }

    // 수강 캘린더 상세
    @GetMapping("/{enrollmentId}/schedule/detail")
    public ResponseEntity<EnrollmentScheduleResponse> getEnrollmentScheduleDetail(
            @PathVariable Long enrollmentId) {
        Enrollment enrollment = findEnrollmentWithRound(enrollmentId);

        Integer durationMin = enrollment.getRound().getCourse().getDurationMin();
        int hours = (durationMin == null || durationMin <= 0) ? 0 : durationMin / 60;

        EnrollmentScheduleResponse response = EnrollmentScheduleResponse.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .userId(enrollment.getUser().getUserId())
                .userName(enrollment.getUser().getName())
                .roundId(enrollment.getRound().getRoundId())
                .roundNo(enrollment.getRound().getRoundNo())
                .courseId(enrollment.getRound().getCourse().getCourseId())
                .courseTitle(enrollment.getRound().getCourse().getTitle())
                .startDate(enrollment.getRound().getStartDate().format(DATE_FORMATTER))
                .endDate(enrollment.getRound().getEndDate().format(DATE_FORMATTER))
                .durationMin(durationMin)
                .trainingHours(hours)
                .status(enrollment.getStatus().name())
                .progress(enrollment.getProgress())
                .build();

        return ResponseEntity.ok(response);
    }

    // ──────────────────────────────────────────────────────────
    // private
    // ──────────────────────────────────────────────────────────

    /** JWT 토큰에서 인증된 사용자의 employeeNo 추출 */
    private String getLoginEmployeeNo() {
        return (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    /** JWT 토큰에서 인증된 사용자의 userId 추출 */
    private Long getLoginUserId() {
        return userRepository.findByEmployeeNo(getLoginEmployeeNo())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."))
                .getUserId();
    }

    private Enrollment findEnrollmentWithRound(Long enrollmentId) {
        return enrollmentRepository.findByIdWithRound(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다."));
    }
}
