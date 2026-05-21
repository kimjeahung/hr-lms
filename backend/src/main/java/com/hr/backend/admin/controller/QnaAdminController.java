package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.QnaAnswerRequest;
import com.hr.backend.admin.dto.QnaAnswerResponse;
import com.hr.backend.admin.dto.QnaQuestionResponse;
import com.hr.backend.domain.qna.service.QnaAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/qna")
@RequiredArgsConstructor
public class QnaAdminController {

    private final QnaAdminService qnaAdminService;

    /** 전체 질문 목록 (unansweredOnly=true 면 미해결만) */
    @GetMapping
    public ResponseEntity<List<QnaQuestionResponse>> getAll(
            @RequestParam(defaultValue = "false") boolean unansweredOnly) {
        return ResponseEntity.ok(qnaAdminService.getAll(unansweredOnly));
    }

    /** 강좌별 질문 목록 */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QnaQuestionResponse>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(qnaAdminService.getByCourse(courseId));
    }

    /** 질문 단건 조회 (답변 포함) */
    @GetMapping("/{questionId}")
    public ResponseEntity<QnaQuestionResponse> getOne(@PathVariable Long questionId) {
        return ResponseEntity.ok(qnaAdminService.getOne(questionId));
    }

    /** 답변 작성 */
    @PostMapping("/{questionId}/answer")
    public ResponseEntity<QnaAnswerResponse> addAnswer(
            @PathVariable Long questionId,
            @RequestBody QnaAnswerRequest req) {
        return ResponseEntity.ok(qnaAdminService.addAnswer(questionId, req));
    }

    /** 답변 수정 */
    @PutMapping("/answer/{answerId}")
    public ResponseEntity<QnaAnswerResponse> updateAnswer(
            @PathVariable Long answerId,
            @RequestBody QnaAnswerRequest req) {
        return ResponseEntity.ok(qnaAdminService.updateAnswer(answerId, req));
    }

    /** 답변 삭제 */
    @DeleteMapping("/answer/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answerId) {
        qnaAdminService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }

    /** 해결 처리 */
    @PutMapping("/{questionId}/resolve")
    public ResponseEntity<QnaQuestionResponse> resolve(@PathVariable Long questionId) {
        return ResponseEntity.ok(qnaAdminService.resolve(questionId));
    }
}
