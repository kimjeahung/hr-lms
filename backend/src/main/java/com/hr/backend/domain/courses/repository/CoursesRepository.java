package com.hr.backend.domain.courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr.backend.domain.courses.entity.Courses;

public interface CoursesRepository extends JpaRepository<Courses, Long> {
    
}
