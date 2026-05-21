package com.hr.backend.domain.qna.controller;

import com.hr.backend.domain.qna.dto.*;
import com.hr.backend.domain.qna.entity.QnaQuestion;
import com.hr.backend.domain.qna.entity.QnaAnswer;
import com.hr.backend.domain.qna.service.QnaService;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaController {
    private final QnaService qnaService;
        private final UserRepository userRepository;

    // 질문 등록
    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<QnaQuestionResponse>> createQuestion(
            @AuthenticationPrincipal String employeeNo,
            @RequestBody QnaQuestionRequest req) {
                Long userId = resolveUserId(employeeNo);
        QnaQuestion q = qnaService.createQuestion(userId, req);
        QnaQuestionResponse resp = toQuestionResponse(q);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<QnaQuestionResponse>builder()
                        .status(201)
                        .message("질문이 등록되었습니다.")
                        .data(resp)
                        .build());
    }

    // 내 질문 목록
    @GetMapping("/questions/my")
    public ResponseEntity<ApiResponse<List<QnaQuestionResponse>>> myQuestions(
            @AuthenticationPrincipal String employeeNo) {
        Long userId = resolveUserId(employeeNo);
        List<QnaQuestion> questions = qnaService.getQuestionsByUser(userId);
        List<QnaQuestionResponse> resp = questions.stream()
                .map(this::toQuestionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.<List<QnaQuestionResponse>>builder()
                .status(200)
                .message("내 질문 목록")
                .data(resp)
                .build());
    }

    // 강좌별 질문 목록
    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<QnaQuestionResponse>>> courseQuestions(
            @RequestParam Long courseId) {
        List<QnaQuestion> questions = qnaService.getQuestionsByCourse(courseId);
        List<QnaQuestionResponse> resp = questions.stream()
                .map(this::toQuestionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.<List<QnaQuestionResponse>>builder()
                .status(200)
                .message("강좌별 질문 목록")
                .data(resp)
                .build());
    }

    // 질문 상세
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<QnaQuestionResponse>> getQuestion(
            @PathVariable Long questionId) {
        QnaQuestion q = qnaService.getQuestion(questionId);
        QnaQuestionResponse resp = toQuestionResponse(q);
        return ResponseEntity.ok(ApiResponse.<QnaQuestionResponse>builder()
                .status(200)
                .message("질문 상세")
                .data(resp)
                .build());
    }

    // 질문 수정
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<QnaQuestionResponse>> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QnaQuestionRequest req) {
        QnaQuestion q = qnaService.updateQuestion(questionId, req);
        QnaQuestionResponse resp = toQuestionResponse(q);
        return ResponseEntity.ok(ApiResponse.<QnaQuestionResponse>builder()
                .status(200)
                .message("질문이 수정되었습니다.")
                .data(resp)
                .build());
    }

    // 질문 삭제
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long questionId) {
        qnaService.deleteQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("질문이 삭제되었습니다.")
                .data(null)
                .build());
    }

    // 답변 등록
    @PostMapping("/answers")
    public ResponseEntity<ApiResponse<QnaAnswerResponse>> createAnswer(
            @AuthenticationPrincipal String employeeNo,
            @RequestBody QnaAnswerRequest req) {
        Long authorId = resolveUserId(employeeNo);
        QnaAnswer a = qnaService.createAnswer(authorId, req);
        QnaAnswerResponse resp = toAnswerResponse(a);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<QnaAnswerResponse>builder()
                        .status(201)
                        .message("답변이 등록되었습니다.")
                        .data(resp)
                        .build());
    }

    // 답변 목록
    @GetMapping("/answers")
    public ResponseEntity<ApiResponse<List<QnaAnswerResponse>>> getAnswers(
            @RequestParam Long questionId) {
        List<QnaAnswer> answers = qnaService.getAnswers(questionId);
        List<QnaAnswerResponse> resp = answers.stream()
                .map(this::toAnswerResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.<List<QnaAnswerResponse>>builder()
                .status(200)
                .message("답변 목록")
                .data(resp)
                .build());
    }

    // 답변 수정
    @PutMapping("/answers/{answerId}")
    public ResponseEntity<ApiResponse<QnaAnswerResponse>> updateAnswer(
            @PathVariable Long answerId,
            @RequestBody QnaAnswerRequest req) {
        QnaAnswer a = qnaService.updateAnswer(answerId, req);
        QnaAnswerResponse resp = toAnswerResponse(a);
        return ResponseEntity.ok(ApiResponse.<QnaAnswerResponse>builder()
                .status(200)
                .message("답변이 수정되었습니다.")
                .data(resp)
                .build());
    }

    // 답변 삭제
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(@PathVariable Long answerId) {
        qnaService.deleteAnswer(answerId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("답변이 삭제되었습니다.")
                .data(null)
                .build());
    }

    private QnaQuestionResponse toQuestionResponse(QnaQuestion q) {
        return QnaQuestionResponse.builder()
                .questionId(q.getQuestionId())
                .courseId(q.getCourse().getCourseId())
                .userId(q.getUser().getUserId())
                .title(q.getTitle())
                .content(q.getContent())
                .isResolved(q.isResolved())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .build();
    }

    private QnaAnswerResponse toAnswerResponse(QnaAnswer a) {
        return QnaAnswerResponse.builder()
                .answerId(a.getAnswerId())
                .questionId(a.getQuestion().getQuestionId())
                .authorId(a.getAuthor().getUserId())
                .content(a.getContent())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

        private Long resolveUserId(String employeeNo) {
                return userRepository.findByEmployeeNo(employeeNo)
                                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."))
                                .getUserId();
        }
}