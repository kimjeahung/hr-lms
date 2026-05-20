package com.hr.backend.domain.quiz.repository;

import com.hr.backend.domain.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByLecture_LectureId(Long lectureId);

    boolean existsByLecture_LectureId(Long lectureId);
}
