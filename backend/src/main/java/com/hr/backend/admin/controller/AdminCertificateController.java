package com.hr.backend.admin.controller;

import com.hr.backend.domain.enrollment.dto.CertificateGenerateRequest;
import com.hr.backend.domain.enrollment.dto.CertificateGenerateResponse;
import com.hr.backend.domain.enrollment.entity.Certificate;
import com.hr.backend.domain.enrollment.repository.CertificateRepository;
import com.hr.backend.domain.enrollment.service.CertificateWorkflowService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 관리자용 이수증 관리 API
 *
 * GET  /api/admin/certificates                     — 전체 이수증 목록
 * GET  /api/admin/certificates/user/{userId}        — 직원별 이수증 목록
 * GET  /api/admin/certificates/course/{courseId}    — 강좌별 이수증 목록
 * POST /api/admin/certificates/generate             — 수동 이수증 발급
 * GET  /api/admin/certificates/{id}/download        — 이수증 PDF 다운로드
 */
@RestController
@RequestMapping("/api/admin/certificates")
@RequiredArgsConstructor
public class AdminCertificateController {

    private final CertificateRepository    certificateRepository;
    private final CertificateWorkflowService certificateWorkflowService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** 전체 이수증 목록 */
    @GetMapping
    public ResponseEntity<List<CertificateDto>> getAll() {
        return ResponseEntity.ok(
                certificateRepository.findAllWithDetails().stream()
                        .map(CertificateDto::from)
                        .toList());
    }

    /** 직원별 이수증 목록 */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                certificateRepository.findAllByUserId(userId).stream()
                        .map(CertificateDto::from)
                        .toList());
    }

    /** 강좌별 이수증 목록 */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CertificateDto>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(
                certificateRepository.findAllByCourseId(courseId).stream()
                        .map(CertificateDto::from)
                        .toList());
    }

    /**
     * 수동 이수증 발급 (관리자 직접 발급).
     * 이미 발급된 경우 기존 정보를 반환한다.
     */
    @PostMapping("/generate")
    public ResponseEntity<CertificateGenerateResponse> generate(
            @RequestBody CertificateGenerateRequest request) {
        return ResponseEntity.ok(certificateWorkflowService.generateCertificate(request));
    }

    /** PDF 다운로드 */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        try {
            Resource resource = certificateWorkflowService.downloadCertificate(id);
            String filename = resource.getFilename() != null ? resource.getFilename() : "certificate.pdf";
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ── 내부 응답 DTO ────────────────────────────────────────

    @Getter
    @Builder
    public static class CertificateDto {
        private Long   certificateId;
        private Long   userId;
        private String employeeNo;
        private String userName;
        private String departmentName;
        private Long   courseId;
        private String courseTitle;
        private Integer roundNo;
        private String issuedAt;
        private String fileUrl;

        public static CertificateDto from(Certificate c) {
            return CertificateDto.builder()
                    .certificateId(c.getCertificateId())
                    .userId(c.getUser().getUserId())
                    .employeeNo(c.getUser().getEmployeeNo())
                    .userName(c.getUser().getName())
                    .departmentName(c.getUser().getDepartment() != null
                            ? c.getUser().getDepartment().getName() : null)
                    .courseId(c.getRound().getCourse().getCourseId())
                    .courseTitle(c.getRound().getCourse().getTitle())
                    .roundNo(c.getRound().getRoundNo())
                    .issuedAt(c.getIssuedAt().format(DATE_FMT))
                    .fileUrl(c.getFileUrl())
                    .build();
        }
    }
}
