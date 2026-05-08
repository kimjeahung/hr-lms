package com.hr.backend.domain.enrollment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificateGenerateRequest {
    private Long userId;
    private Long courseId;
}
