package com.hr.backend.domain.course.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CourseDetailResponse {
    private Long courseId;
    private String title;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Integer durationMin;
    
    // 강의 목록
    private List<LectureWithProgressResponse> lectures;
    
    // 퀴즈/시험/설문 정보
    private QuizSummary quiz;
    private ExamSummary exam;
    private SurveySummary survey;
    
    @Getter
    @Builder
    public static class QuizSummary {
        private Long quizId;
        private String title;
        private Integer passScore;
        private Integer questionCount;
        private Boolean completed;
        private Integer score;
    }
    
    @Getter
    @Builder
    public static class ExamSummary {
        private Long examId;
        private String title;
        private Integer passScore;
        private Integer questionCount;
        private Boolean completed;
        private Integer score;
    }
    
    @Getter
    @Builder
    public static class SurveySummary {
        private Long surveyId;
        private String title;
        private Boolean completed;
    }
}