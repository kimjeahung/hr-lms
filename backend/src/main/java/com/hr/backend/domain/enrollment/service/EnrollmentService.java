package com.hr.backend.domain.enrollment.service;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.course.repository.CourseRoundRepository;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository       userRepository;
    private final CourseRoundRepository courseRoundRepository;

    /** 전체 이수 현황 조회 (관리자용) */
    public List<EnrollmentResponse> getAll() {
        return enrollmentRepository.findAllWithUserAndCourse().stream()
                .map(EnrollmentResponse::new)
                .toList();
    }

    /** 특정 직원 이수 현황 */
    public List<EnrollmentResponse> getByUser(Long userId) {
        return enrollmentRepository.findAllByUserId(userId).stream()
                .map(EnrollmentResponse::new)
                .toList();
    }

    /** 특정 차수 수강자 현황 */
    public List<EnrollmentResponse> getByCourse(Long courseId) {
        return enrollmentRepository.findAllByCourseId(courseId).stream()
                .map(EnrollmentResponse::new)
                .toList();
    }

    /** 수강 등록 (관리자용 - 차수 기반, 신청 즉시 APPROVED + IN_PROGRESS) */
    @Transactional
    public EnrollmentResponse enroll(Long userId, Long roundId) {
        if (enrollmentRepository.existsByUser_UserIdAndRound_RoundId(userId, roundId)) {
            throw new IllegalArgumentException("이미 수강 등록된 차수입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        CourseRound round = courseRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("차수를 찾을 수 없습니다."));

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .round(round)
                .build();
        enrollment.approve();
        enrollment.changeStatus(Enrollment.Status.IN_PROGRESS);
        return new EnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    /** 수강 신청 (사용자용 - 신청 즉시 IN_PROGRESS, 승인 절차 없음) */
    @Transactional
    public EnrollmentResponse applyEnrollment(Long userId, Long roundId) {
        if (enrollmentRepository.existsByUser_UserIdAndRound_RoundId(userId, roundId)) {
            throw new IllegalArgumentException("이미 수강 신청된 차수입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        CourseRound round = courseRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("차수를 찾을 수 없습니다."));

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .round(round)
                .build();
        enrollment.changeStatus(Enrollment.Status.IN_PROGRESS);
        return new EnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    /** 진행률 업데이트 */
    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, int progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다."));
        enrollment.updateProgress(progress);
        return new EnrollmentResponse(enrollment);
    }

    /** 수강 상세 조회 */
    public EnrollmentResponse getEnrollmentById(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));
        return new EnrollmentResponse(enrollment);
    }

    /** 수강 완료 처리 */
    @Transactional
    public EnrollmentResponse completeEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));
        enrollment.updateProgress(100);
        return new EnrollmentResponse(enrollment);
    }

    /** 수강 상태 변경 */
    @Transactional
    public EnrollmentResponse changeEnrollmentStatus(Long enrollmentId, String status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));
        try {
            Enrollment.Status newStatus = Enrollment.Status.valueOf(status);
            enrollment.changeStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 상태값입니다. 유효한 값: " + java.util.Arrays.toString(Enrollment.Status.values()));
        }
        return new EnrollmentResponse(enrollment);
    }

    /** 수강중인 교육 조회 */
    public List<EnrollmentResponse> getOngoingEnrollments(Long userId) {
        return enrollmentRepository.findAllByUserId(userId).stream()
                .filter(e -> e.getStatus() == Enrollment.Status.IN_PROGRESS)
                .map(EnrollmentResponse::new)
                .toList();
    }

    /** 수강 통계 조회 */
    public Map<String, Object> getEnrollmentStatistics() {
        List<Enrollment> all = enrollmentRepository.findAll();
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", (long) all.size());
        stats.put("notStarted", all.stream().filter(e -> e.getStatus() == Enrollment.Status.NOT_STARTED).count());
        stats.put("inProgress", all.stream().filter(e -> e.getStatus() == Enrollment.Status.IN_PROGRESS).count());
        stats.put("completed", all.stream().filter(e -> e.getStatus() == Enrollment.Status.DONE).count());
        return stats;
    }

    /** 차수 일정 변경 (마감일 업데이트) */
    @Transactional
    public CourseRound updateRoundSchedule(Long roundId, LocalDate endDate) {
        CourseRound round = courseRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("차수를 찾을 수 없습니다."));
        round.update(round.getStartDate(), endDate);
        return courseRoundRepository.save(round);
    }
}
