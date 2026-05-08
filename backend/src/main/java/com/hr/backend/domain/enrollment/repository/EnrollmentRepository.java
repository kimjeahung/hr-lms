package com.hr.backend.domain.enrollment.repository;

import com.hr.backend.domain.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.course WHERE e.user.id = :userId")
    List<Enrollment> findAllByUserId(Long userId);

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.course WHERE e.course.courseId = :courseId")
    List<Enrollment> findAllByCourseId(Long courseId);

    Optional<Enrollment> findByUser_IdAndCourse_CourseId(Long userId, Long courseId);

    boolean existsByUser_IdAndCourse_CourseId(Long userId, Long courseId);

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.course")
    List<Enrollment> findAllWithUserAndCourse();
}
