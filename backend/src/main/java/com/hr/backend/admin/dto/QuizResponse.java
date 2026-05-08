package com.hr.backend.admin.dto;

import com.hr.backend.domain.quiz.entity.Quiz;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class QuizResponse {

    private final Long quizId;
    private final Long lectureId;
    private final String lectureTitle;
    private final String title;
    private final int passScore;
    private final List<QuestionResponse> questions;
    private final LocalDateTime createdAt;

    public QuizResponse(Quiz quiz) {
        this.quizId       = quiz.getQuizId();
        this.lectureId    = quiz.getLecture().getLectureId();
        this.lectureTitle = quiz.getLecture().getTitle();
        this.title        = quiz.getTitle();
        this.passScore    = quiz.getPassScore();
        this.questions    = quiz.getQuestions().stream()
                .map(QuestionResponse::new)
                .toList();
        this.createdAt    = quiz.getCreatedAt();
    }
}
