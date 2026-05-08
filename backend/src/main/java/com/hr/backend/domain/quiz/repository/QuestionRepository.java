package com.hr.backend.domain.quiz.repository;

import com.hr.backend.domain.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
