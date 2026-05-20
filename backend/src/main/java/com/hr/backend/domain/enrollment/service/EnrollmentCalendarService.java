package com.hr.backend.domain.enrollment.service;

import com.hr.backend.domain.enrollment.dto.EnrollmentCalendarResponse;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
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

    /**
     * 사용자의 전체 수강 일정 조회
     * 모든 상태의 수강 정보 반환 (끝난 것, 진행중, 예정)
     */
    public List<EnrollmentCalendarResponse> getUserEnrollmentCalendar(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByUserId(userId);
        
        return enrollments.stream()
                .map(enrollment -> EnrollmentCalendarResponse.builder()
                        .enrollmentId(enrollment.getEnrollmentId())
                        .courseId(enrollment.getRound().getCourse().getCourseId())
                        .courseTitle(enrollment.getRound().getCourse().getTitle())
                        .category(enrollment.getRound().getCourse().getCategory())
                        .startDate(enrollment.getRound().getStartDate())
                        .endDate(enrollment.getRound().getEndDate())
                        .status(enrollment.getStatus().name())
                        .progress(enrollment.getProgress())
                        .build()
                )
                .collect(Collectors.toList());
    }
}