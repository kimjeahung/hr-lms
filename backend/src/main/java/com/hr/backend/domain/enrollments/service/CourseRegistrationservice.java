package com.hr.backend.domain.enrollments.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr.backend.domain.courses.entity.Courses;
import com.hr.backend.domain.courses.repository.CoursesRepository;
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
        enrollment.setStatus("ENROLLED");
        enrollment.setStarted_at(new Date());

        return enrollmentsRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<Enrollments> getEnrollmentHistory(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return enrollmentsRepository.findByUser_id(user);
    }

    @Transactional
    public Enrollments manageCourseSessions(Long enrollmentId, int progress) {
        Enrollments enrollment = enrollmentsRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강 내역입니다."));

        enrollment.setProgress(progress);
        if (progress >= 100) {
            enrollment.setStatus("COMPLETED");
            enrollment.setCompleted_at(new Date());
        }

        return enrollmentsRepository.save(enrollment);
    }

    @Transactional
    public Courses manageEnrollmentSchedule(Long courseId, Date deadline) {
        Courses course = coursesRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        course.setDeadline(deadline);
        return coursesRepository.save(course);
    }
}
