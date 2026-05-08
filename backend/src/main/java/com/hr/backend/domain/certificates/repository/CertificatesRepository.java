package com.hr.backend.domain.certificates.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr.backend.domain.certificates.entity.Certificates;

public interface CertificatesRepository extends JpaRepository<Certificates, Long> {
    // 자격증 관련 데이터베이스 작업을 위한 메서드 정의

    
}
