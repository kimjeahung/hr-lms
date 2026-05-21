package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.dto.response.DashboardResponse;
import com.hr.backend.employee.service.EmployeeDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/user/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmployeeDashboardController {

    private final EmployeeDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<CommonResponse<DashboardResponse>> getDashboard() {
        DashboardResponse dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(CommonResponse.success("대시보드 데이터를 성공적으로 조회했습니다.", dashboardData));
    }
}