package com.hr.backend.domain.enrollment.service;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.course.repository.CourseRoundRepository;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.enums.EnrollmentStatus;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    /** 수강 등록 (차수 기반, 신청 즉시 APPROVED) */
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
        enrollment.approve(); // 신청 즉시 승인
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

    //
     //수강 신청
    @Transactional
    public EnrollmentResponse registerCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Courses course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        if (enrollmentRepository.existsByUser_idAndCourse_id(user, course)) {
            throw new IllegalStateException("이미 수강 신청된 강의입니다.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser_id(user);
        enrollment.setCourse_id(course);
        enrollment.setProgress(0);
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS.name());
        enrollment.setStarted_at(new Date());

        return enrollmentRepository.save(enrollment);
    }

    //수강 이력 조회
    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentHistory(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return enrollmentRepository.findByUser_id(user);
    }

    //수강 진행 관리
    @Transactional
    public Enrollment manageCourseSessions(Long enrollmentId, int progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));

        enrollment.setProgress(progress);
        if (progress >= 100) {
            enrollment.setStatus(EnrollmentStatus.DONE.name());
            enrollment.setCompleted_at(new Date());
        } else {
            enrollment.setStatus(EnrollmentStatus.IN_PROGRESS.name());
        }

        return enrollmentRepository.save(enrollment);
    }

    //수강 일정 관리
    @Transactional
    public Course manageEnrollmentSchedule(Long courseId, Date deadline) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        course.setDeadline(deadline);
        return courseRepository.save(course);
    }

    //수강 상세 조회
    @Transactional(readOnly = true)
    public Enrollment getEnrollmentById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));
    }

    //수강 완료 처리
    @Transactional
    public Enrollment completeEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));
        enrollment.setProgress(100);
        enrollment.setStatus(EnrollmentStatus.DONE.name());
        enrollment.setCompleted_at(new Date());
        return enrollmentRepository.save(enrollment);
    }

    //수강 상태 변경
    @Transactional
    public Enrollment changeEnrollmentStatus(Long enrollmentId, String status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));
        try {
            EnrollmentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 상태값입니다. 유효한 값: " + java.util.Arrays.toString(EnrollmentStatus.values()));
        }
        enrollment.setStatus(status);
        return enrollmentRepository.save(enrollment);
    }

    //수강중인 교육 조회
    @Transactional(readOnly = true)
    public List<Enrollment> getOngoingEnrollments(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return enrollmentRepository.findByUser_idAndStatus(user, EnrollmentStatus.IN_PROGRESS.name());
    }

    //특정 유저 수강 내역 조회(본인수강 내역 조회)
    @Transactional(readOnly = true)
    public List<Enrollment> getALLEnrollmentsByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return enrollmentRepository.findByUser_id(user);
    }

    //수강 통계 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getEnrollmentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", enrollmentRepository.count());
        stats.put("notStarted", enrollmentRepository.countByStatus(EnrollmentStatus.NOT_STARTED.name()));
        stats.put("inProgress", enrollmentRepository.countByStatus(EnrollmentStatus.IN_PROGRESS.name()));
        stats.put("completed", enrollmentRepository.countByStatus(EnrollmentStatus.DONE.name()));
        return stats;
    }
}
