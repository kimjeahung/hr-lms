package com.hr.backend.domain.enrollment.controller;

import com.hr.backend.domain.enrollment.dto.CertificateActionResponse;
import com.hr.backend.domain.enrollment.dto.CertificateFailRequest;
import com.hr.backend.domain.enrollment.dto.CertificateGenerateRequest;
import com.hr.backend.domain.enrollment.dto.CertificateGenerateResponse;
import com.hr.backend.domain.enrollment.service.CertificateGenerationException;
import com.hr.backend.domain.enrollment.service.CertificateWorkflowService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateWorkflowService certificateService;

    @Value("${certificate.internal-api-token:change-me-in-prod}")
    private String internalApiToken;

    @PostMapping("/generate")
    public ResponseEntity<CertificateGenerateResponse> generate(
            @RequestHeader(value = "X-Internal-Token", required = false) String token,
            @RequestBody CertificateGenerateRequest request) {
        if (!isValidInternalToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    CertificateGenerateResponse.builder()
                            .success(false)
                            .message("인증되지 않은 내부 요청입니다.")
                            .build()
            );
        }
        try {
            return ResponseEntity.ok(certificateService.generateCertificate(request));
        } catch (IllegalArgumentException | CertificateGenerationException e) {
            return ResponseEntity.badRequest().body(
                    CertificateGenerateResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/fail")
    public ResponseEntity<CertificateActionResponse> fail(
            @RequestHeader(value = "X-Internal-Token", required = false) String token,
            @RequestBody CertificateFailRequest request) {
        if (!isValidInternalToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CertificateActionResponse.builder()
                            .success(false)
                            .message("인증되지 않은 내부 요청입니다.")
                            .build());
        }
        return ResponseEntity.ok(certificateService.handleFailure(request));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        try {
            Resource resource = certificateService.downloadCertificate(id);
            String filename = resource.getFilename() != null ? resource.getFilename() : "certificate.pdf";
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private boolean isValidInternalToken(String token) {
        return token != null && !token.isBlank() && token.equals(internalApiToken);
    }
}
