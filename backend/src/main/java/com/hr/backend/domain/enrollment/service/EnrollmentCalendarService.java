package com.hr.backend.domain.enrollment.service;

import com.hr.backend.domain.enrollment.dto.EnrollmentCalendarResponse;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.course.repository.CourseRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentCalendarService {
    
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRoundRepository courseRoundRepository;

    /**
     * 전체 강의 일정 + 내 수강상태 포함 반환
     */
    public List<EnrollmentCalendarResponse> getAllRoundsWithMyStatus(Long userId) {
        List<CourseRound> rounds = courseRoundRepository.findAll();
        return rounds.stream().map(round -> {
            Enrollment enrollment = enrollmentRepository.findByUser_UserIdAndRound_RoundId(userId, round.getRoundId()).orElse(null);
            return EnrollmentCalendarResponse.builder()
                    .roundId(round.getRoundId())
                    .courseId(round.getCourse().getCourseId())
                    .courseTitle(round.getCourse().getTitle())
                    .category(round.getCourse().getCategory())
                    .startDate(round.getStartDate())
                    .endDate(round.getEndDate())
                    .enrollmentId(enrollment != null ? enrollment.getEnrollmentId() : null)
                    .myStatus(enrollment != null ? enrollment.getStatus().name() : "NONE")
                    .myProgress(enrollment != null ? enrollment.getProgress() : null)
                    .build();
        }).collect(Collectors.toList());
    }
}