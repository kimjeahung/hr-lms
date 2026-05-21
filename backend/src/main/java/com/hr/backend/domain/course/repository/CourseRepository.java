package com.hr.backend.domain.course.repository;

import com.hr.backend.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByActiveTrue();

    /** 전체 목록 대신 COUNT만 조회 — DashboardService 최적화 */
    long countByActiveTrue();
}
