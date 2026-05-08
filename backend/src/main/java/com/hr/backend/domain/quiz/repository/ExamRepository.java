package com.hr.backend.domain.quiz.repository;

import com.hr.backend.domain.quiz.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    Optional<Exam> findByCourse_CourseId(Long courseId);

    boolean existsByCourse_CourseId(Long courseId);
}
