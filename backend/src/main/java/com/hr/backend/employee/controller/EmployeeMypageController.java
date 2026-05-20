package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.dto.response.MypageResponse;
import com.hr.backend.employee.service.EmployeeMypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/mypage")
@RequiredArgsConstructor
public class EmployeeMypageController {

    private final EmployeeMypageService mypageService;

    @GetMapping
    public ResponseEntity<CommonResponse<MypageResponse>> getMyPageInfo() {
        MypageResponse mypageInfo = mypageService.getMyPageInfo();
        return ResponseEntity.ok(CommonResponse.success("내 기본 정보를 성공적으로 조회했습니다.", mypageInfo));
    }
}