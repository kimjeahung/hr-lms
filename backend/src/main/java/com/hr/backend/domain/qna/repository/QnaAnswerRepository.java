package com.hr.backend.domain.qna.repository;

import com.hr.backend.domain.qna.entity.QnaAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Long> {
    List<QnaAnswer> findByQuestionId(Long questionId);
}