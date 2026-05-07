package com.hr.backend.domain.enrollments.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr.backend.domain.courses.entity.Courses;
import com.hr.backend.domain.courses.repository.CoursesRepository;
import com.hr.backend.domain.enrollments.EnrollmentEnum;
import com.hr.backend.domain.enrollments.entity.Enrollments;
import com.hr.backend.domain.enrollments.repository.EnrollmentsRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseRegistrationservice {

    private final EnrollmentsRepository enrollmentsRepository;
    private final CoursesRepository coursesRepository;
    private final UserRepository userRepository;

    //수강 신청
    @Transactional
    public Enrollments registerCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Courses course = coursesRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        if (enrollmentsRepository.existsByUser_idAndCourse_id(user, course)) {
            throw new IllegalStateException("이미 수강 신청된 강의입니다.");
        }

        Enrollments enrollment = new Enrollments();
        enrollment.setUser_id(user);
        enrollment.setCourse_id(course);
        enrollment.setProgress(0);
        enrollment.setStatus(EnrollmentEnum.IN_PROGRESS.name());
        enrollment.setStarted_at(new Date());

        return enrollmentsRepository.save(enrollment);
    }

    //수강 이력 조회
    @Transactional(readOnly = true)
    public List<Enrollments> getEnrollmentHistory(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return enrollmentsRepository.findByUser_id(user);
    }

    //수강 진행 관리
    @Transactional
    public Enrollments manageCourseSessions(Long enrollmentId, int progress) {
        Enrollments enrollment = enrollmentsRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));

        enrollment.setProgress(progress);
        if (progress >= 100) {
            enrollment.setStatus(EnrollmentEnum.DONE.name());
            enrollment.setCompleted_at(new Date());
        } else {
            enrollment.setStatus(EnrollmentEnum.IN_PROGRESS.name());
        }

        return enrollmentsRepository.save(enrollment);
    }

    //수강 일정 관리
    @Transactional
    public Courses manageEnrollmentSchedule(Long courseId, Date deadline) {
        Courses course = coursesRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        course.setDeadline(deadline);
        return coursesRepository.save(course);
    }

    //수강 상세 조회
    @Transactional(readOnly = true)
    public Enrollments getEnrollmentById(Long enrollmentId) {
        return enrollmentsRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));
    }

    //수강 완료 처리
    @Transactional
    public Enrollments completeEnrollment(Long enrollmentId) {
        Enrollments enrollment = enrollmentsRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));
        enrollment.setProgress(100);
        enrollment.setStatus(EnrollmentEnum.DONE.name());
        enrollment.setCompleted_at(new Date());
        return enrollmentsRepository.save(enrollment);
    }

    //수강 상태 변경
    @Transactional
    public Enrollments changeEnrollmentStatus(Long enrollmentId, String status) {
        Enrollments enrollment = enrollmentsRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));
        try {
            EnrollmentEnum.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 상태값입니다. 유효한 값: " + java.util.Arrays.toString(EnrollmentEnum.values()));
        }
        enrollment.setStatus(status);
        return enrollmentsRepository.save(enrollment);
    }

    //수강중인 교육 조회
    @Transactional(readOnly = true)
    public List<Enrollments> getOngoingEnrollments(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return enrollmentsRepository.findByUser_idAndStatus(user, EnrollmentEnum.IN_PROGRESS.name());
    }

    //특정 유저 수강 내역 조회(본인수강 내역 조회)
    @Transactional(readOnly = true)
    public List<Enrollments> getALLEnrollmentsByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return enrollmentsRepository.findByUser_id(user);
    }

    //수강 통계 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getEnrollmentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", enrollmentsRepository.count());
        stats.put("notStarted", enrollmentsRepository.countByStatus(EnrollmentEnum.NOT_STARTED.name()));
        stats.put("inProgress", enrollmentsRepository.countByStatus(EnrollmentEnum.IN_PROGRESS.name()));
        stats.put("completed", enrollmentsRepository.countByStatus(EnrollmentEnum.DONE.name()));
        return stats;
    }
}
