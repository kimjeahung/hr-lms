package com.hr.backend.domain.enrollment.service;

import com.hr.backend.domain.enrollment.dto.FeedbackRequest;
import com.hr.backend.domain.enrollment.dto.FeedbackResponse;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.entity.EnrollmentFeedback;
import com.hr.backend.domain.enrollment.repository.EnrollmentFeedbackRepository;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final EnrollmentFeedbackRepository feedbackRepository;
    private final EnrollmentRepository         enrollmentRepository;
    private final UserRepository               userRepository;

    /**
     * 피드백 제출 (이미 제출했으면 수정, 없으면 신규 저장).
     * 수강 완료(DONE) 상태여야만 제출 가능.
     */
    @Transactional
    public FeedbackResponse submitFeedback(Long enrollmentId, String employeeNo, FeedbackRequest req) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다."));

        if (enrollment.getStatus() != Enrollment.Status.DONE) {
            throw new IllegalStateException("수강 완료 후에만 피드백을 제출할 수 있습니다.");
        }

        User user = userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        // 본인 수강 건인지 확인
        if (!enrollment.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("본인의 수강 건에만 피드백을 제출할 수 있습니다.");
        }

        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new IllegalArgumentException("별점은 1~5 사이여야 합니다.");
        }

        EnrollmentFeedback feedback = feedbackRepository
                .findByEnrollment_EnrollmentId(enrollmentId)
                .orElse(null);

        if (feedback == null) {
            feedback = EnrollmentFeedback.builder()
                    .enrollment(enrollment)
                    .user(user)
                    .rating(req.getRating())
                    .comment(req.getComment())
                    .build();
        } else {
            feedback.update(req.getRating(), req.getComment());
        }

        return new FeedbackResponse(feedbackRepository.save(feedback));
    }

    /** 특정 수강 건의 피드백 조회 */
    public FeedbackResponse getFeedback(Long enrollmentId) {
        EnrollmentFeedback feedback = feedbackRepository
                .findByEnrollment_EnrollmentId(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("피드백이 없습니다."));
        return new FeedbackResponse(feedback);
    }
}
