package com.hr.backend.domain.enrollment.repository;

import com.hr.backend.domain.enrollment.entity.EnrollmentFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentFeedbackRepository extends JpaRepository<EnrollmentFeedback, Long> {

    Optional<EnrollmentFeedback> findByEnrollment_EnrollmentId(Long enrollmentId);
}
