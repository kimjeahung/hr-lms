package com.hr.backend.domain.quiz.controller;

import com.hr.backend.domain.quiz.dto.AttemptRequest;
import com.hr.backend.domain.quiz.dto.AttemptResponse;
import com.hr.backend.domain.quiz.service.AttemptService;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService  attemptService;
    private final UserRepository  userRepository;

    // ──────────────────────────────────────────────────────────
    // 퀴즈 응시
    // ──────────────────────────────────────────────────────────

    /** 퀴즈 제출 */
    @PostMapping("/api/user/lectures/{lectureId}/quiz/submit")
    public ResponseEntity<AttemptResponse> submitQuiz(
            @PathVariable Long lectureId,
            @RequestBody AttemptRequest req) {
        Long userId = getLoginUserId();
        return ResponseEntity.ok(attemptService.submitQuiz(userId, lectureId, req));
    }

    /** 퀴즈 응시 이력 조회 */
    @GetMapping("/api/user/lectures/{lectureId}/quiz/attempts")
    public ResponseEntity<List<AttemptResponse>> getQuizAttempts(@PathVariable Long lectureId) {
        Long userId = getLoginUserId();
        return ResponseEntity.ok(attemptService.getQuizAttempts(userId, lectureId));
    }

    // ──────────────────────────────────────────────────────────
    // 시험 응시
    // ──────────────────────────────────────────────────────────

    /** 시험 제출 */
    @PostMapping("/api/user/courses/{courseId}/exam/submit")
    public ResponseEntity<AttemptResponse> submitExam(
            @PathVariable Long courseId,
            @RequestBody AttemptRequest req) {
        Long userId = getLoginUserId();
        return ResponseEntity.ok(attemptService.submitExam(userId, courseId, req));
    }

    /** 시험 응시 이력 조회 */
    @GetMapping("/api/user/courses/{courseId}/exam/attempts")
    public ResponseEntity<List<AttemptResponse>> getExamAttempts(@PathVariable Long courseId) {
        Long userId = getLoginUserId();
        return ResponseEntity.ok(attemptService.getExamAttempts(userId, courseId));
    }

    // ──────────────────────────────────────────────────────────
    // private
    // ──────────────────────────────────────────────────────────

    private Long getLoginUserId() {
        String employeeNo = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."))
                .getUserId();
    }
}
