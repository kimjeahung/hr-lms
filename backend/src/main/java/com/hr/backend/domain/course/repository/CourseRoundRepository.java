package com.hr.backend.domain.course.repository;

import com.hr.backend.domain.course.entity.CourseRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRoundRepository extends JpaRepository<CourseRound, Long> {

    List<CourseRound> findAllByCourse_CourseIdOrderByRoundNoAsc(Long courseId);

    boolean existsByCourse_CourseIdAndRoundNo(Long courseId, int roundNo);
}
