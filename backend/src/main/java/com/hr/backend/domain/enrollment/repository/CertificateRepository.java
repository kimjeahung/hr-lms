package com.hr.backend.domain.enrollment.repository;

import com.hr.backend.domain.enrollment.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    boolean existsByUser_UserIdAndRound_RoundId(Long userId, Long roundId);

    Optional<Certificate> findByUser_UserIdAndRound_RoundId(Long userId, Long roundId);

    List<Certificate> findByUser_UserId(Long userId);
}
