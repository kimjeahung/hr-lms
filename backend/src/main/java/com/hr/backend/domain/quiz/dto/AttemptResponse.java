package com.hr.backend.domain.quiz.dto;

import com.hr.backend.domain.quiz.entity.Attempt;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AttemptResponse {

    private final Long   attemptId;
    private final Long   userId;
    private final int    score;
    private final boolean passed;
    private final LocalDateTime attemptedAt;

    // 퀴즈 응시일 때
    private final Long   quizId;
    private final String quizTitle;

    // 시험 응시일 때
    private final Long   examId;
    private final String examTitle;

    public AttemptResponse(Attempt attempt) {
        this.attemptId   = attempt.getAttemptId();
        this.userId      = attempt.getUser().getUserId();
        this.score       = attempt.getScore();
        this.passed      = attempt.isPassed();
        this.attemptedAt = attempt.getAttemptedAt();

        if (attempt.getQuiz() != null) {
            this.quizId    = attempt.getQuiz().getQuizId();
            this.quizTitle = attempt.getQuiz().getTitle();
            this.examId    = null;
            this.examTitle = null;
        } else {
            this.quizId    = null;
            this.quizTitle = null;
            this.examId    = attempt.getExam().getExamId();
            this.examTitle = attempt.getExam().getTitle();
        }
    }
}
