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

    // 로그인한 사용자의 전체 응시 이력 조회
    // - 퀴즈 응시와 시험 응시를 모두 포함
    // - attemptedAt 기준 최신순 정렬
    // - 마이페이지, 대시보드, 내 시험/퀴즈 결과 목록에서 사용
    List<Attempt> findAllByUser_UserIdOrderByAttemptedAtDesc(Long userId);

    // 특정 사용자가 특정 강의(lecture)에 연결된 퀴즈를 통과했는지 확인
    // - Quiz → Lecture 관계를 따라 lectureId 기준으로 조회
    // - 강의 완료 처리 시 사용
    // - 예: 영상 시청 완료 + 퀴즈 통과이면 lecture_progress 완료 처리
    boolean existsByUser_UserIdAndQuiz_Lecture_LectureIdAndPassedTrue(
            Long userId,
            Long lectureId
    );

    // 특정 사용자가 특정 강좌(course)에 연결된 시험을 통과했는지 확인
    // - Exam → Course 관계를 따라 courseId 기준으로 조회
    // - 수강 완료 및 이수증 발급 조건 확인 시 사용
    // - 예: 모든 강의 완료 + 시험 통과 + 설문 제출 후 이수 완료 처리
    boolean existsByUser_UserIdAndExam_Course_CourseIdAndPassedTrue(
            Long userId,
            Long courseId
    );
}
