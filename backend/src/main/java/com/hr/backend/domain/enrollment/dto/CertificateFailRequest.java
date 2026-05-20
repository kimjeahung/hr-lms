package com.hr.backend.domain.enrollment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificateFailRequest {
    private Long userId;
    private String reason;
}
