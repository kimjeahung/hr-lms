package com.hr.backend.domain.enrollment.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CertificatePdfService {

    private final TemplateEngine templateEngine;

    @Value("${certificate.storage-path:/app/certificates}")
    private String storagePath;

    /**
     * Thymeleaf 템플릿을 렌더링한 뒤 OpenHTMLToPDF로 PDF를 생성하고 저장 경로를 반환한다.
     *
     * @param variables 템플릿 변수 맵
     * @param fileName  저장할 파일명 (예: cert_1.pdf)
     * @param subDir    연도 서브 디렉토리 (예: "2026")
     * @return 저장된 파일의 절대 경로
     */
    public Path generatePdf(Map<String, Object> variables, String fileName, String subDir) {
        // 1. Thymeleaf → HTML 문자열
        Context ctx = new Context();
        ctx.setVariables(variables);
        String htmlContent = templateEngine.process("certificate", ctx);

        // 2. 저장 디렉토리 생성
        Path dir = Paths.get(storagePath, subDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new CertificateGenerationException("이수증 디렉토리 생성 실패: " + dir, e);
        }

        // 3. PDF 생성
        Path pdfFile = dir.resolve(fileName);
        try (OutputStream os = Files.newOutputStream(pdfFile)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            throw new CertificateGenerationException("PDF 변환 실패: " + e.getMessage(), e);
        }

        return pdfFile;
    }
}
