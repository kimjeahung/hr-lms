package com.hr.backend.employee.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CertificateResponse {
    private Long certificateId;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime issuedAt;
    private String fileURL;
}