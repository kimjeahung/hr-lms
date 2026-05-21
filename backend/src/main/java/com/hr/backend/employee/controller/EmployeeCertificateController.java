package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.dto.response.CertificateResponse;
import com.hr.backend.employee.service.EmployeeCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/certificates")
@RequiredArgsConstructor
public class EmployeeCertificateController {

    private final EmployeeCertificateService certificateService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<CertificateResponse>>> getMyCertificates() {
        List<CertificateResponse> certificates = certificateService.getMyCertificates();
        return ResponseEntity.ok(CommonResponse.success("내 이수증 목록을 성공적으로 조회했습니다.", certificates));
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<CommonResponse<CertificateResponse>> getCertificateDetail(@PathVariable Long certificateId) {
        CertificateResponse certificateDetail = certificateService.getCertificateDetail(certificateId);
        return ResponseEntity.ok(CommonResponse.success("이수증 상세 정보를 성공적으로 조회했습니다.", certificateDetail));
    }
}