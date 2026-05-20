package com.hr.backend.domain.enrollment.controller;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.enrollment.dto.EnrollmentCalendarResponse;
import com.hr.backend.domain.enrollment.dto.EnrollmentScheduleResponse;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.service.EnrollmentCalendarService;
import com.hr.backend.domain.enrollment.service.EnrollmentService;
import com.hr.backend.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/user/enrollments")
@RequiredArgsConstructor
public class EnrollmentUserController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentCalendarService enrollmentCalendarService;
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
    
        /**
     * 수강신청 취소 (본인)
     */
    @DeleteMapping("/cancel/{enrollmentId}")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long enrollmentId) {
        Long loginUserId = getLoginUserId();
        Enrollment enrollment = findEnrollmentWithRound(enrollmentId);
        if (!enrollment.getUser().getUserId().equals(loginUserId) && !isAdmin()) {
            throw new IllegalArgumentException("본인 또는 관리자만 취소할 수 있습니다.");
        }
        enrollmentService.cancelEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }

    // 본인 수강 이력 조회 (JWT 기준)
    @GetMapping("/history")
    public ResponseEntity<List<EnrollmentResponse>> enrollmentHistory() {
        return ResponseEntity.ok(enrollmentService.getHistoryByUser(getLoginUserId()));
    }

    // 본인 전체 수강 내역 조회 (JWT 기준)
    @GetMapping("/all")
    public ResponseEntity<List<EnrollmentResponse>> getALLEnrollmentsByUser() {
        return ResponseEntity.ok(enrollmentService.getByUser(getLoginUserId()));
    }

    // 본인 수강중인 교육 조회 (JWT 기준)
    @GetMapping("/ongoing")
    public ResponseEntity<List<EnrollmentResponse>> getOngoingEnrollments() {
        return ResponseEntity.ok(enrollmentService.getOngoingEnrollments(getLoginUserId()));
    }

    // 하위호환용 경로 (기존 클라이언트 유지)
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> enrollmentHistoryLegacy(@PathVariable Long userId) {
        if (!isAdmin() && !getLoginUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 수강 이력만 조회할 수 있습니다.");
        }
        return ResponseEntity.ok(enrollmentService.getHistoryByUser(userId));
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getALLEnrollmentsByUserLegacy(@PathVariable Long userId) {
        if (!isAdmin() && !getLoginUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 전체 수강 내역만 조회할 수 있습니다.");
        }
        return ResponseEntity.ok(enrollmentService.getByUser(userId));
    }

    @GetMapping("/ongoing/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getOngoingEnrollmentsLegacy(@PathVariable Long userId) {
        if (!isAdmin() && !getLoginUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 수강중인 교육만 조회할 수 있습니다.");
        }
        return ResponseEntity.ok(enrollmentService.getOngoingEnrollments(userId));
    }

    // 수강 상세 조회
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentDetails(@PathVariable Long enrollmentId) {
        Long loginUserId = getLoginUserId();
        return ResponseEntity.ok(enrollmentService.getEnrollmentByIdForActor(enrollmentId, loginUserId, isAdmin()));
    }

    // 수강 진행 관리 (진행률 업데이트)
    @PutMapping("/{enrollmentId}/progress")
    public ResponseEntity<EnrollmentResponse> updateProgress(
            @PathVariable Long enrollmentId,
            @RequestParam int progress) {
        Long loginUserId = getLoginUserId();
        return ResponseEntity.ok(enrollmentService.updateProgressForActor(enrollmentId, progress, loginUserId, isAdmin()));
    }

    // 수강 완료 처리
    @PutMapping("/{enrollmentId}/complete")
    public ResponseEntity<EnrollmentResponse> completeEnrollment(@PathVariable Long enrollmentId) {
        Long loginUserId = getLoginUserId();
        return ResponseEntity.ok(enrollmentService.completeEnrollmentForActor(enrollmentId, loginUserId, isAdmin()));
    }

    // 수강 캘린더 (전체)
    @GetMapping("/schedule")
    public ResponseEntity<List<EnrollmentCalendarResponse>> getEnrollmentSchedule() {
        return ResponseEntity.ok(enrollmentCalendarService.getAllRoundsWithMyStatus(getLoginUserId()));
    }

    // 수강 캘린더 상세
    @GetMapping("/{enrollmentId}/schedule/detail")
    public ResponseEntity<EnrollmentScheduleResponse> getEnrollmentScheduleDetail(
            @PathVariable Long enrollmentId) {
        Long loginUserId = getLoginUserId();
        enrollmentService.validateEnrollmentAccess(enrollmentId, loginUserId, isAdmin());
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

    /** 관리자 여부 확인 */
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Enrollment findEnrollmentWithRound(Long enrollmentId) {
        return entityManager.createQuery(
                        "select e from Enrollment e " +
                        "join fetch e.user u " +
                        "join fetch e.round r " +
                        "join fetch r.course c " +
                        "where e.enrollmentId = :id", Enrollment.class)
                .setParameter("id", enrollmentId)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다."));
    }
}
