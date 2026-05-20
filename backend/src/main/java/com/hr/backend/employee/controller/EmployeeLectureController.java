package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.service.EmployeeLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/lectures")
@RequiredArgsConstructor
public class EmployeeLectureController {
    private final EmployeeLectureService lectureService;

    @PutMapping("/{lectureId}/complete")
    public ResponseEntity<CommonResponse<?>> completeLecture(@PathVariable Long lectureId) {
        lectureService.completeLecture(lectureId);
        return ResponseEntity.ok(CommonResponse.success("강의를 완료 처리했습니다."));
    }
}
