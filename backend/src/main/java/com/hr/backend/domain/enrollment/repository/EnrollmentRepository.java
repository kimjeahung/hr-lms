package com.hr.backend.domain.enrollment.repository;

import com.hr.backend.domain.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.round r JOIN FETCH r.course WHERE e.user.userId = :userId")
    List<Enrollment> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.round r JOIN FETCH r.course WHERE r.course.courseId = :courseId")
    List<Enrollment> findAllByCourseId(@Param("courseId") Long courseId);

    Optional<Enrollment> findByUser_UserIdAndRound_RoundId(Long userId, Long roundId);

    boolean existsByUser_UserIdAndRound_RoundId(Long userId, Long roundId);

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user u LEFT JOIN FETCH u.department JOIN FETCH e.round r JOIN FETCH r.course")
    List<Enrollment> findAllWithUserAndCourse();

    // ── 필터용 쿼리 ──────────────────────────────────────────

    /** 부서명으로 필터 */
    @Query("""
        SELECT e FROM Enrollment e
        JOIN FETCH e.user u
        LEFT JOIN FETCH u.department d
        JOIN FETCH e.round r
        JOIN FETCH r.course c
        WHERE d.name = :deptName
        """)
    List<Enrollment> findAllByDepartment(@Param("deptName") String deptName);

    /** 강좌 카테고리로 필터 (법정의무교육 / 직무교육) */
    @Query("""
        SELECT e FROM Enrollment e
        JOIN FETCH e.user u
        LEFT JOIN FETCH u.department
        JOIN FETCH e.round r
        JOIN FETCH r.course c
        WHERE c.category = :category
        """)
    List<Enrollment> findAllByCategory(@Param("category") String category);

    /** targetRole로 필터 (1=현장직, 2=사무직) */
    @Query("""
        SELECT e FROM Enrollment e
        JOIN FETCH e.user u
        LEFT JOIN FETCH u.department
        JOIN FETCH e.round r
        JOIN FETCH r.course c
        WHERE c.targetRole = :targetRole
        """)
    List<Enrollment> findAllByTargetRole(@Param("targetRole") int targetRole);

    /** 미이수자만 — DONE이 아닌 수강 내역 */
    @Query("""
        SELECT e FROM Enrollment e
        JOIN FETCH e.user u
        LEFT JOIN FETCH u.department
        JOIN FETCH e.round r
        JOIN FETCH r.course c
        WHERE e.status <> 'DONE'
        """)
    List<Enrollment> findAllNotCompleted();
}
