package com.hr.backend.employee.service;

import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.entity.CourseVideo;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import com.hr.backend.employee.dto.response.MyCourseResponse;
import com.hr.backend.employee.exception.ResourceNotFoundException;
import com.hr.backend.employee.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeMyCourseService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public Page<MyCourseResponse> getMyCourses(Pageable pageable) {
        List<MyCourseResponse> list = enrollmentRepository.findAllByUserId(getCurrentUser().getUserId()).stream().map(this::toResponse).toList();
        int start=(int)Math.min(pageable.getOffset(), list.size()); int end=Math.min(start+pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start,end), pageable, list.size());
    }

    public MyCourseResponse.MyCourseDetailResponse getMyCourseDetail(Long courseId) {
        Enrollment e = enrollmentRepository.findAllByUserId(getCurrentUser().getUserId()).stream()
                .filter(x -> x.getRound().getCourse().getCourseId().equals(courseId)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "courseId", courseId));
        Course c = e.getRound().getCourse();
        List<MyCourseResponse.MyCourseVideoWatchStatusDto> videos = c.getLectures().stream().flatMap(l -> l.getVideos().stream())
                .sorted(Comparator.comparingInt(CourseVideo::getSortOrder))
                .map(v -> MyCourseResponse.MyCourseVideoWatchStatusDto.builder().videoId(v.getVideoId()).title(v.getTitle())
                        .videoURL(v.getVideoUrl()).durationSec(v.getDurationSec()).sortOrder(v.getSortOrder())
                        .watchedSec(0).isCompleted(false).build()).toList();
        return MyCourseResponse.MyCourseDetailResponse.builder().enrollmentId(e.getEnrollmentId()).courseId(c.getCourseId())
                .courseTitle(c.getTitle()).courseDescription(c.getDescription()).courseCategory(c.getCategory())
                .courseThumbnailUrl(c.getThumbnailUrl()).currentProgress(e.getProgress()).currentStatus(e.getStatus().name())
                .courseDeadline(e.getRound().getEndDate()).enrolledAt(e.getEnrolledAt()).completedAt(e.getCompletedAt()).videos(videos).build();
    }
    private MyCourseResponse toResponse(Enrollment e){Course c=e.getRound().getCourse();return MyCourseResponse.builder().enrollmentId(e.getEnrollmentId()).courseId(c.getCourseId()).courseTitle(c.getTitle()).courseThumbnailUrl(c.getThumbnailUrl()).progress(e.getProgress()).status(e.getStatus().name()).deadline(e.getRound().getEndDate()).enrolledAt(e.getEnrolledAt()).completedAt(e.getCompletedAt()).build();}
    private User getCurrentUser(){Long id=currentUserProvider.getCurrentUserId();return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "userId", id));}
}
