package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.dto.response.MyCourseResponse;
import com.hr.backend.employee.service.EmployeeMyCourseService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/my-courses")
@RequiredArgsConstructor
public class EmployeeMyCourseController {

    private final EmployeeMyCourseService myCourseService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<MyCourseResponse>>> getMyCourses(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "enrolledAt,desc") String[] sort
    ) {
        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<MyCourseResponse> myCourses = myCourseService.getMyCourses(pageable);
        return ResponseEntity.ok(CommonResponse.success("내가 수강 신청한 강의 목록을 성공적으로 조회했습니다.", myCourses));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CommonResponse<MyCourseResponse.MyCourseDetailResponse>> getMyCourseDetail(@PathVariable Long courseId) {
        MyCourseResponse.MyCourseDetailResponse myCourseDetail = myCourseService.getMyCourseDetail(courseId);
        return ResponseEntity.ok(CommonResponse.success("내 특정 강의 학습 상세 정보를 성공적으로 조회했습니다.", myCourseDetail));
    }
}