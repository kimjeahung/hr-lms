package com.hr.backend.domain.enrollment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CertificateGenerateResponse {
    private boolean success;
    private Long certificateId;
    private String pdfPath;
    private String certificateNo;
    private String message;
}
