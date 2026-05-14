package com.hr.backend.domain.qna.repository;

import com.hr.backend.domain.qna.entity.QnaQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QnaQuestionRepository extends JpaRepository<QnaQuestion, Long> {
    List<QnaQuestion> findByCourseId(Long courseId);
    List<QnaQuestion> findByUserId(Long userId);
}