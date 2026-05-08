package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.ExamRequest;
import com.hr.backend.admin.dto.ExamResponse;
import com.hr.backend.admin.dto.QuestionRequest;
import com.hr.backend.domain.quiz.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/courses/{courseId}/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    /** 강좌별 시험 조회 */
    @GetMapping
    public ResponseEntity<ExamResponse> get(@PathVariable Long courseId) {
        return ResponseEntity.ok(examService.getByCourse(courseId));
    }

    /** 시험 생성 */
    @PostMapping
    public ResponseEntity<ExamResponse> create(
            @PathVariable Long courseId, @RequestBody ExamRequest req) {
        return ResponseEntity.ok(examService.create(courseId, req));
    }

    /** 시험 수정 */
    @PutMapping
    public ResponseEntity<ExamResponse> update(
            @PathVariable Long courseId, @RequestBody ExamRequest req) {
        return ResponseEntity.ok(examService.update(courseId, req));
    }

    /** 시험 삭제 */
    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long courseId) {
        examService.delete(courseId);
        return ResponseEntity.noContent().build();
    }

    /** 문항 추가 */
    @PostMapping("/questions")
    public ResponseEntity<ExamResponse> addQuestion(
            @PathVariable Long courseId, @RequestBody QuestionRequest req) {
        return ResponseEntity.ok(examService.addQuestion(courseId, req));
    }

    /** 문항 수정 */
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<ExamResponse> updateQuestion(
            @PathVariable Long courseId,
            @PathVariable Long questionId,
            @RequestBody QuestionRequest req) {
        return ResponseEntity.ok(examService.updateQuestion(courseId, questionId, req));
    }

    /** 문항 삭제 */
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<ExamResponse> deleteQuestion(
            @PathVariable Long courseId, @PathVariable Long questionId) {
        return ResponseEntity.ok(examService.deleteQuestion(courseId, questionId));
    }
}
