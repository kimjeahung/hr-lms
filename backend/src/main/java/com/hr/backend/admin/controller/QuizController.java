package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.QuestionRequest;
import com.hr.backend.admin.dto.QuizRequest;
import com.hr.backend.admin.dto.QuizResponse;
import com.hr.backend.domain.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/lectures/{lectureId}/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /** 강의별 퀴즈 조회 */
    @GetMapping
    public ResponseEntity<QuizResponse> get(@PathVariable Long lectureId) {
        return ResponseEntity.ok(quizService.getByLecture(lectureId));
    }

    /** 퀴즈 생성 */
    @PostMapping
    public ResponseEntity<QuizResponse> create(
            @PathVariable Long lectureId, @RequestBody QuizRequest req) {
        return ResponseEntity.ok(quizService.create(lectureId, req));
    }

    /** 퀴즈 수정 */
    @PutMapping
    public ResponseEntity<QuizResponse> update(
            @PathVariable Long lectureId, @RequestBody QuizRequest req) {
        return ResponseEntity.ok(quizService.update(lectureId, req));
    }

    /** 퀴즈 삭제 */
    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long lectureId) {
        quizService.delete(lectureId);
        return ResponseEntity.noContent().build();
    }

    /** 문항 추가 */
    @PostMapping("/questions")
    public ResponseEntity<QuizResponse> addQuestion(
            @PathVariable Long lectureId, @RequestBody QuestionRequest req) {
        return ResponseEntity.ok(quizService.addQuestion(lectureId, req));
    }

    /** 문항 수정 */
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuizResponse> updateQuestion(
            @PathVariable Long lectureId,
            @PathVariable Long questionId,
            @RequestBody QuestionRequest req) {
        return ResponseEntity.ok(quizService.updateQuestion(lectureId, questionId, req));
    }

    /** 문항 삭제 */
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<QuizResponse> deleteQuestion(
            @PathVariable Long lectureId, @PathVariable Long questionId) {
        return ResponseEntity.ok(quizService.deleteQuestion(lectureId, questionId));
    }
}
