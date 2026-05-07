package com.hr.backend.domain.enrollments.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr.backend.domain.courses.entity.Courses;
import com.hr.backend.domain.enrollments.entity.Enrollments;
import com.hr.backend.domain.user.entity.User;

public interface EnrollmentsRepository extends JpaRepository<Enrollments, Long> {
    List<Enrollments> findByUser_id(User user);
    boolean existsByUser_idAndCourse_id(User user, Courses course);
}
