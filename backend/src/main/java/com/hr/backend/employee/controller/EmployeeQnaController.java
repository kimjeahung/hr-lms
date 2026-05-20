package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.dto.request.QnaQuestionRequest;
import com.hr.backend.employee.dto.response.QnaResponse;
import com.hr.backend.employee.service.EmployeeQnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/qna/questions")
@RequiredArgsConstructor
public class EmployeeQnaController {
    private final EmployeeQnaService qnaService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<QnaResponse>>> getMyQuestions(@RequestParam(required = false) Long courseId) {
        List<QnaResponse> result = courseId == null ? qnaService.getMyQuestions() : qnaService.getCourseQuestions(courseId);
        return ResponseEntity.ok(CommonResponse.success("QnA 목록을 조회했습니다.", result));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<CommonResponse<QnaResponse>> getQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(CommonResponse.success("QnA 상세를 조회했습니다.", qnaService.getQuestion(questionId)));
    }

    @GetMapping("/{questionId}/answers")
    public ResponseEntity<CommonResponse<List<QnaResponse.AnswerItem>>> getAnswers(@PathVariable Long questionId) {
        return ResponseEntity.ok(CommonResponse.success("QnA 답변을 조회했습니다.", qnaService.getQuestion(questionId).getAnswers()));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<QnaResponse>> createQuestion(@Valid @RequestBody QnaQuestionRequest request) {
        return ResponseEntity.ok(CommonResponse.success("질문을 등록했습니다.", qnaService.createQuestion(request)));
    }
}
