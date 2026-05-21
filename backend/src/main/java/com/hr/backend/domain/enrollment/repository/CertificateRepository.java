package com.hr.backend.domain.enrollment.repository;

import com.hr.backend.domain.enrollment.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    boolean existsByUser_UserIdAndRound_RoundId(Long userId, Long roundId);

    Optional<Certificate> findByUser_UserIdAndRound_RoundId(Long userId, Long roundId);

    /** 전체 이수증 목록 (관리자용) — 사용자·차수·강좌 JOIN FETCH */
    @Query("""
        SELECT c FROM Certificate c
        JOIN FETCH c.user u
        LEFT JOIN FETCH u.department
        JOIN FETCH c.round r
        JOIN FETCH r.course
        ORDER BY c.issuedAt DESC
        """)
    List<Certificate> findAllWithDetails();

    /** 특정 직원의 이수증 목록 */
    @Query("""
        SELECT c FROM Certificate c
        JOIN FETCH c.user u
        LEFT JOIN FETCH u.department
        JOIN FETCH c.round r
        JOIN FETCH r.course
        WHERE c.user.userId = :userId
        ORDER BY c.issuedAt DESC
        """)
    List<Certificate> findAllByUserId(@Param("userId") Long userId);

    /** 특정 강좌의 이수증 목록 */
    @Query("""
        SELECT c FROM Certificate c
        JOIN FETCH c.user u
        LEFT JOIN FETCH u.department
        JOIN FETCH c.round r
        JOIN FETCH r.course co
        WHERE co.courseId = :courseId
        ORDER BY c.issuedAt DESC
        """)
    List<Certificate> findAllByCourseId(@Param("courseId") Long courseId);
}
