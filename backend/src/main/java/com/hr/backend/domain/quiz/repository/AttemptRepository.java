package com.hr.backend.domain.quiz.repository;

import com.hr.backend.domain.quiz.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    // 퀴즈 응시 이력 (최신순)
    List<Attempt> findByUser_UserIdAndQuiz_QuizIdOrderByAttemptedAtDesc(Long userId, Long quizId);

    // 시험 응시 이력 (최신순)
    List<Attempt> findByUser_UserIdAndExam_ExamIdOrderByAttemptedAtDesc(Long userId, Long examId);

    // 퀴즈 최근 응시 1건
    Optional<Attempt> findTopByUser_UserIdAndQuiz_QuizIdOrderByAttemptedAtDesc(Long userId, Long quizId);

    // 시험 최근 응시 1건
    Optional<Attempt> findTopByUser_UserIdAndExam_ExamIdOrderByAttemptedAtDesc(Long userId, Long examId);

    // 퀴즈 통과 여부 확인
    boolean existsByUser_UserIdAndQuiz_QuizIdAndPassedTrue(Long userId, Long quizId);

    // 시험 통과 여부 확인
    boolean existsByUser_UserIdAndExam_ExamIdAndPassedTrue(Long userId, Long examId);
}
