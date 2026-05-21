package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.dto.request.AnswerSubmitRequest;
import com.hr.backend.employee.dto.response.AssessmentResponse;
import com.hr.backend.employee.dto.response.AttemptResponse;
import com.hr.backend.employee.service.EmployeeAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/exams")
@RequiredArgsConstructor
public class EmployeeExamController {
    private final EmployeeAssessmentService assessmentService;

    @GetMapping("/{courseId}")
    public ResponseEntity<CommonResponse<AssessmentResponse>> getExam(@PathVariable Long courseId) {
        return ResponseEntity.ok(CommonResponse.success("시험을 조회했습니다.", assessmentService.getExamByCourse(courseId)));
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<CommonResponse<AttemptResponse>> submitExam(@PathVariable Long examId, @Valid @RequestBody AnswerSubmitRequest request) {
        return ResponseEntity.ok(CommonResponse.success("시험 답안을 제출했습니다.", assessmentService.submitExam(examId, request)));
    }

    @GetMapping("/results")
    public ResponseEntity<CommonResponse<List<AttemptResponse>>> getMyResults() {
        return ResponseEntity.ok(CommonResponse.success("내 응시 결과를 조회했습니다.", assessmentService.getMyAttempts()));
    }
}
