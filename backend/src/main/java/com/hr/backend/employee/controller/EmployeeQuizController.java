package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.dto.request.AnswerSubmitRequest;
import com.hr.backend.employee.dto.response.AssessmentResponse;
import com.hr.backend.employee.dto.response.AttemptResponse;
import com.hr.backend.employee.service.EmployeeAssessmentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/quizzes")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class EmployeeQuizController {
    private final EmployeeAssessmentService assessmentService;

    @GetMapping("/{lectureId}")
    public ResponseEntity<CommonResponse<AssessmentResponse>> getQuiz(@PathVariable Long lectureId) {
        return ResponseEntity.ok(CommonResponse.success("퀴즈를 조회했습니다.", assessmentService.getQuizByLecture(lectureId)));
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<CommonResponse<AttemptResponse>> submitQuiz(@PathVariable Long quizId, @Valid @RequestBody AnswerSubmitRequest request) {
        return ResponseEntity.ok(CommonResponse.success("퀴즈 답안을 제출했습니다.", assessmentService.submitQuiz(quizId, request)));
    }

    @GetMapping("/results")
    public ResponseEntity<CommonResponse<List<AttemptResponse>>> getMyResults() {
        return ResponseEntity.ok(CommonResponse.success("내 응시 결과를 조회했습니다.", assessmentService.getMyAttempts()));
    }
}
