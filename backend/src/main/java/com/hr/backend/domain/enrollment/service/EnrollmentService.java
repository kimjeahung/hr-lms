package com.hr.backend.domain.enrollment.service;

import com.hr.backend.admin.dto.EnrollmentResponse;
import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository       userRepository;
    private final CourseRepository     courseRepository;

    // 전체 이수 현황 조회 (관리자용)
    public List<EnrollmentResponse> getAll() {
        return enrollmentRepository.findAllWithUserAndCourse().stream()
                .map(EnrollmentResponse::new)
                .toList();
    }

    // 특정 직원 이수 현황
    public List<EnrollmentResponse> getByUser(Long userId) {
        return enrollmentRepository.findAllByUserId(userId).stream()
                .map(EnrollmentResponse::new)
                .toList();
    }

    // 특정 강의 수강자 현황
    public List<EnrollmentResponse> getByCourse(Long courseId) {
        return enrollmentRepository.findAllByCourseId(courseId).stream()
                .map(EnrollmentResponse::new)
                .toList();
    }

    // 수강 등록
    @Transactional
    public EnrollmentResponse enroll(Long userId, Long courseId) {
        if (enrollmentRepository.existsByUser_IdAndCourse_CourseId(userId, courseId)) {
            throw new IllegalArgumentException("이미 수강 등록된 강의입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        return new EnrollmentResponse(enrollmentRepository.save(
                Enrollment.builder().user(user).course(course).build()));
    }

    // 진행률 업데이트
    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, int progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다."));
        enrollment.updateProgress(progress);
        return new EnrollmentResponse(enrollment);
    }
}
