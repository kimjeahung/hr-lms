package com.hr.backend.domain.course.repository;

import com.hr.backend.domain.course.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    List<Lecture> findAllByCourse_CourseIdOrderBySortOrderAsc(Long courseId);
}
