package com.hr.backend.domain.quiz.repository;

import com.hr.backend.domain.quiz.entity.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {

    /** 특정 응시의 전체 문항별 답안 조회 */
    List<AttemptAnswer> findAllByAttempt_AttemptId(Long attemptId);

    /** 특정 응시의 오답만 조회 */
    List<AttemptAnswer> findAllByAttempt_AttemptIdAndCorrectFalse(Long attemptId);
}
