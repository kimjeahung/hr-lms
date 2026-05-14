package com.hr.backend.domain.enrollment.controller;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.enrollment.dto.EnrollmentScheduleResponse;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.service.EnrollmentService;
import com.hr.backend.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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

    private final EnrollmentService enrollmentService;
    private final EntityManager     entityManager;
    private final UserRepository    userRepository;

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

    // 수강 피드백 제출 (stub — 추후 구현)
    @PostMapping("/{enrollmentId}/feedback")
    public ResponseEntity<String> submitEnrollmentFeedback(
            @PathVariable Long enrollmentId,
            @RequestParam String feedback) {
        return ResponseEntity.ok("피드백이 제출되었습니다.");
    }

    // 수강 알림 설정 (stub — 추후 구현)
    @PostMapping("/{enrollmentId}/notifications")
    public ResponseEntity<String> setEnrollmentNotifications(
            @PathVariable Long enrollmentId,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok("알림 설정이 변경되었습니다: " + enabled);
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

    /** JWT 토큰에서 인증된 사용자의 userId 추출 */
    private Long getLoginUserId() {
        String employeeNo = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."))
                .getUserId();
    }

    private Enrollment findEnrollmentWithRound(Long enrollmentId) {
        try {
            return entityManager.createQuery(
                            "select e from Enrollment e " +
                            "join fetch e.user u " +
                            "join fetch e.round r " +
                            "join fetch r.course c " +
                            "where e.enrollmentId = :id", Enrollment.class)
                    .setParameter("id", enrollmentId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new IllegalArgumentException("수강 정보를 찾을 수 없습니다.");
        }
    }
}
