package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.service.EmployeeCourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/courses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmployeeCourseController {

    private final EmployeeCourseService courseService;

    /*
     * 강좌 목록 조회와 강좌 상세 조회는
     * 기존 CourseUserController에서 처리합니다.
     *
     * GET /api/user/courses
     * GET /api/user/courses/{courseId}
     *
     * 따라서 여기서는 중복 매핑을 제거합니다.
     */

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<CommonResponse<?>> enrollCourse(@PathVariable Long courseId) {
        courseService.enrollCourse(courseId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success("강의 수강 신청이 완료되었습니다."));
    }
}