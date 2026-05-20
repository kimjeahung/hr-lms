package com.hr.backend.domain.enrollment.service;

import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.course.repository.CourseRoundRepository;
import com.hr.backend.domain.enrollment.dto.CertificateActionResponse;
import com.hr.backend.domain.enrollment.dto.CertificateFailRequest;
import com.hr.backend.domain.enrollment.dto.CertificateGenerateRequest;
import com.hr.backend.domain.enrollment.dto.CertificateGenerateResponse;
import com.hr.backend.domain.enrollment.entity.Certificate;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.CertificateRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateWorkflowService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRoundRepository courseRoundRepository;
    private final CertificatePdfService certificatePdfService;
    private final RestClient restClient = RestClient.create();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    @Value("${certificate.storage-path:/app/certificates}")
    private String certificateStoragePath;

    @Value("${certificate.n8n-webhook-url:}")
    private String n8nWebhookUrl;

    @Value("${certificate.trigger-enabled:true}")
    private boolean triggerEnabled;

    @Value("${certificate.issuer-name:인사하는 인사팀}")
    private String issuerName;

    public void triggerCompletionWorkflow(Enrollment enrollment) {
        if (!triggerEnabled || n8nWebhookUrl == null || n8nWebhookUrl.isBlank()) {
            return;
        }
        if (enrollment.getStatus() != Enrollment.Status.DONE) {
            return;
        }
        if (certificateRepository.existsByUser_UserIdAndRound_RoundId(
                enrollment.getUser().getUserId(),
                enrollment.getRound().getRoundId())) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("requestId", UUID.randomUUID().toString());
        payload.put("userId", enrollment.getUser().getUserId());
        payload.put("courseId", enrollment.getRound().getCourse().getCourseId());
        payload.put("roundId", enrollment.getRound().getRoundId());
        payload.put("enrollmentId", enrollment.getEnrollmentId());

        restClient.post()
                .uri(n8nWebhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Transactional
    public CertificateGenerateResponse generateCertificate(CertificateGenerateRequest request) {
        if (request == null || request.getUserId() == null || request.getCourseId() == null) {
            throw new IllegalArgumentException("userId와 courseId는 필수입니다.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<CourseRound> rounds = courseRoundRepository.findAllByCourse_CourseIdOrderByRoundNoAsc(request.getCourseId());
        if (rounds.isEmpty()) {
            throw new IllegalArgumentException("해당 강의의 차수를 찾을 수 없습니다.");
        }

        CourseRound round = rounds.get(rounds.size() - 1);

        // 이미 발급된 이수증이면 재발급 없이 기존 정보 반환
        Certificate existing = certificateRepository
                .findByUser_UserIdAndRound_RoundId(user.getUserId(), round.getRoundId())
                .orElse(null);
        if (existing != null) {
            return CertificateGenerateResponse.builder()
                    .success(true)
                    .certificateId(existing.getCertificateId())
                    .pdfPath(existing.getFileUrl())
                    .certificateNo(buildCertificateNo(existing))
                    .message("이미 발급된 이수증입니다.")
                    .build();
        }

        // 1. 먼저 DB 저장해 certificateId 확보 (fileUrl은 이후 업데이트)
        Certificate saved = certificateRepository.save(
                Certificate.builder().user(user).round(round).fileUrl("").build());

        // 2. 이수증 번호 조합
        String certNo = buildCertificateNo(saved);

        // 3. 템플릿 변수 구성
        String currentYear = String.valueOf(Year.now().getValue());
        Map<String, Object> vars = new HashMap<>();
        vars.put("certificateNo", certNo);
        vars.put("userName", user.getName());
        vars.put("department", resolveDepartmentName(user));
        vars.put("courseTitle", round.getCourse().getTitle());
        vars.put("startDate", round.getStartDate().format(DATE_FORMAT));
        vars.put("endDate", round.getEndDate().format(DATE_FORMAT));
        vars.put("trainingHours", resolveTrainingHours(round));
        vars.put("issuedAt", saved.getIssuedAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        vars.put("issuerName", issuerName);

        // 4. PDF 생성
        String fileName = "cert_" + saved.getCertificateId() + ".pdf";
        Path pdfPath = certificatePdfService.generatePdf(vars, fileName, currentYear);

        // 5. 저장 경로 업데이트
        String storedPath = "/certificates/" + currentYear + "/" + fileName;
        saved.updateFileUrl(storedPath);
        certificateRepository.save(saved);

        return CertificateGenerateResponse.builder()
                .success(true)
                .certificateId(saved.getCertificateId())
                .pdfPath(storedPath)
                .certificateNo(certNo)
                .message("이수증이 생성되었습니다.")
                .build();
    }

    public CertificateActionResponse handleFailure(CertificateFailRequest request) {
        String reason = request != null && request.getReason() != null && !request.getReason().isBlank()
                ? request.getReason()
                : "이수증 처리 실패";
        return CertificateActionResponse.builder()
                .success(false)
                .message(reason)
                .build();
    }

    public Resource downloadCertificate(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이수증을 찾을 수 없습니다."));
        if (certificate.getFileUrl() == null || certificate.getFileUrl().isBlank()) {
            throw new IllegalArgumentException("저장된 이수증 파일이 없습니다.");
        }

        Path fullPath = resolveStoragePath(certificate.getFileUrl());
        if (!Files.exists(fullPath)) {
            throw new IllegalArgumentException("이수증 파일이 존재하지 않습니다.");
        }
        return new PathResource(fullPath);
    }

    // ──────────────────────────────────────────────────────────
    // private helpers
    // ──────────────────────────────────────────────────────────

    private String buildCertificateNo(Certificate certificate) {
        return Year.now().getValue() + "-"
                + certificate.getRound().getCourse().getCourseId() + "-"
                + String.format("%04d", certificate.getCertificateId());
    }

    private String resolveDepartmentName(User user) {
        if (user.getDepartment() == null) return "-";
        return user.getDepartment().getName();
    }

    private int resolveTrainingHours(CourseRound round) {
        Integer durationMin = round.getCourse().getDurationMin();
        if (durationMin == null || durationMin <= 0) return 0;
        return durationMin / 60;
    }

    private Path resolveStoragePath(String storedPath) {
        String relative = storedPath.startsWith("/") ? storedPath.substring(1) : storedPath;
        if (relative.startsWith("certificates/")) {
            relative = relative.substring("certificates/".length());
        }
        return Paths.get(certificateStoragePath, relative);
    }
}
