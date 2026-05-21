package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.AttemptResponse;
import com.hr.backend.domain.quiz.entity.Attempt;
import com.hr.backend.domain.quiz.repository.AttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자용 퀴즈/시험 응시 결과 조회 API.
 *
 * GET /api/admin/attempts/course/{courseId}/exam         — 강좌 시험 응시 목록
 * GET /api/admin/attempts/course/{courseId}/exam/stats   — 강좌 시험 합격률 통계
 * GET /api/admin/attempts/course/{courseId}/quiz         — 강좌 퀴즈 응시 목록
 * GET /api/admin/attempts/user/{userId}                  — 직원별 전체 응시 이력
 */
@RestController
@RequestMapping("/api/admin/attempts")
@RequiredArgsConstructor
public class AdminAttemptController {

    private final AttemptRepository attemptRepository;

    /** 강좌 시험 응시 전체 목록 */
    @GetMapping("/course/{courseId}/exam")
    public ResponseEntity<List<AttemptResponse>> getExamAttemptsByCourse(
            @PathVariable Long courseId) {
        List<AttemptResponse> result = attemptRepository
                .findAllByExam_Course_CourseIdOrderByAttemptedAtDesc(courseId)
                .stream()
                .map(AttemptResponse::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * 강좌 시험 합격률 통계.
     * 응시자 수 / 합격자 수 / 합격률(%) / 평균 점수 반환.
     */
    @GetMapping("/course/{courseId}/exam/stats")
    public ResponseEntity<Map<String, Object>> getExamStats(@PathVariable Long courseId) {
        List<Attempt> attempts = attemptRepository
                .findAllByExam_Course_CourseIdOrderByAttemptedAtDesc(courseId);

        long total  = attempts.size();
        long passed = attempts.stream().filter(Attempt::isPassed).count();
        double passRate  = total == 0 ? 0 : Math.round(passed * 1000.0 / total) / 10.0;
        double avgScore  = total == 0 ? 0
                : Math.round(attempts.stream().mapToInt(Attempt::getScore).average().orElse(0) * 10) / 10.0;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalAttempts",  total);
        stats.put("passedCount",    passed);
        stats.put("failedCount",    total - passed);
        stats.put("passRate",       passRate);
        stats.put("averageScore",   avgScore);
        return ResponseEntity.ok(stats);
    }

    /** 강좌 퀴즈 응시 전체 목록 */
    @GetMapping("/course/{courseId}/quiz")
    public ResponseEntity<List<AttemptResponse>> getQuizAttemptsByCourse(
            @PathVariable Long courseId) {
        List<AttemptResponse> result = attemptRepository
                .findAllByQuiz_Lecture_Course_CourseIdOrderByAttemptedAtDesc(courseId)
                .stream()
                .map(AttemptResponse::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    /** 직원별 전체 응시 이력 (퀴즈 + 시험) */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttemptResponse>> getAttemptsByUser(@PathVariable Long userId) {
        List<AttemptResponse> result = attemptRepository
                .findAllByUser_UserIdOrderByAttemptedAtDesc(userId)
                .stream()
                .map(AttemptResponse::new)
                .toList();
        return ResponseEntity.ok(result);
    }
}
