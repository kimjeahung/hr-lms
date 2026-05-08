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

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.round r JOIN FETCH r.course")
    List<Enrollment> findAllWithUserAndCourse();
}
