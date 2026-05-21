package com.hr.backend.admin.dto;

import com.hr.backend.domain.quiz.entity.Attempt;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AttemptResponse {

    private final Long   attemptId;
    private final Long   userId;
    private final String userName;
    private final String employeeNo;
    private final String department;
    private final String targetType;   // EXAM | QUIZ
    private final Long   targetId;     // examId or quizId
    private final String targetTitle;  // 시험명 or 퀴즈명
    private final String courseTitle;
    private final int    score;
    private final boolean passed;
    private final LocalDateTime attemptedAt;

    public AttemptResponse(Attempt a) {
        this.attemptId   = a.getAttemptId();
        this.userId      = a.getUser().getUserId();
        this.userName    = a.getUser().getName();
        this.employeeNo  = a.getUser().getEmployeeNo();
        this.department  = a.getUser().getDepartment() != null
                           ? a.getUser().getDepartment().getName() : null;
        this.score       = a.getScore();
        this.passed      = a.isPassed();
        this.attemptedAt = a.getAttemptedAt();

        if (a.getExam() != null) {
            this.targetType  = "EXAM";
            this.targetId    = a.getExam().getExamId();
            this.targetTitle = a.getExam().getTitle();
            this.courseTitle = a.getExam().getCourse().getTitle();
        } else if (a.getQuiz() != null) {
            this.targetType  = "QUIZ";
            this.targetId    = a.getQuiz().getQuizId();
            this.targetTitle = a.getQuiz().getTitle();
            this.courseTitle = a.getQuiz().getLecture().getCourse().getTitle();
        } else {
            this.targetType  = "UNKNOWN";
            this.targetId    = null;
            this.targetTitle = null;
            this.courseTitle = null;
        }
    }
}
