package com.hr.backend.domain.enrollment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CertificateActionResponse {
    private boolean success;
    private String message;
}
