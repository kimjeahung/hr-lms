package com.hr.backend.domain.qna.repository;

import com.hr.backend.domain.qna.entity.QnaQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaQuestionRepository extends JpaRepository<QnaQuestion, Long> {

    List<QnaQuestion> findAllByOrderByCreatedAtDesc();

    List<QnaQuestion> findAllByResolvedFalseOrderByCreatedAtDesc();

    List<QnaQuestion> findAllByCourse_CourseIdOrderByCreatedAtDesc(Long courseId);
}
