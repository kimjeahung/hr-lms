package com.hr.backend.domain.course.service;

import com.hr.backend.domain.course.dto.CourseDetailResponse;
import com.hr.backend.domain.course.dto.CourseListItemResponse;
import com.hr.backend.domain.course.dto.LectureWithProgressResponse;
import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.course.repository.CourseRoundRepository;
import com.hr.backend.domain.course.repository.LectureRepository;
import com.hr.backend.domain.course.repository.LectureProgressRepository;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseUserService {
    
    private final CourseRepository courseRepository;
    private final CourseRoundRepository courseRoundRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final LectureProgressRepository lectureProgressRepository;

    /**
     * 강좌 목록 조회 (사용자용 - 신청 상태 포함)
     */
    public List<CourseListItemResponse> getCourseList(Long userId) {
        // 모든 활성화된 강좌 조회
        List<Course> courses = courseRepository.findAllByActiveTrue();
        
        // 사용자의 수강 현황 조회
        List<Enrollment> enrollments = userId == null
                ? Collections.emptyList()
                : enrollmentRepository.findAllByUserId(userId);
        Map<Long, Enrollment> enrollmentMap = enrollments.stream()
                .collect(Collectors.toMap(e -> e.getRound().getCourse().getCourseId(), e -> e));
        
        return courses.stream()
                .flatMap(course -> courseRoundRepository.findAllByCourse_CourseIdOrderByRoundNoAsc(course.getCourseId()).stream()
                        .map(round -> {
                            Enrollment enrollment = enrollmentMap.get(course.getCourseId());
                            String status = enrollment == null ? "NOT_ENROLLED" : enrollment.getStatus().name();
                            
                            return CourseListItemResponse.builder()
                                    .courseId(course.getCourseId())
                                    .title(course.getTitle())
                                    .description(course.getDescription())
                                    .category(course.getCategory())
                                    .thumbnailUrl(course.getThumbnailUrl())
                                    .durationMin(course.getDurationMin())
                                    .roundId(round.getRoundId())
                                    .roundNo(round.getRoundNo())
                                    .startDate(round.getStartDate())
                                    .endDate(round.getEndDate())
                                    .enrollmentStatus(status)
                                    .build();
                        })
                )
                .collect(Collectors.toList());
    }

    /**
     * 강좌 상세 조회 (강의/퀴즈/시험/설문 상태 포함)
     */
    public CourseDetailResponse getCourseDetail(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강좌를 찾을 수 없습니다."));
        
        // 강의 목록 with 시청률
        var lectures = lectureRepository.findAllByCourse_CourseIdOrderBySortOrderAsc(courseId).stream()
                .map(lecture -> buildLectureWithProgress(userId, lecture))
                .collect(Collectors.toList());
        
        // TODO: 퀴즈, 시험, 설문 정보 조회 (데이터베이스 구조에 따라)
        
        return CourseDetailResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .thumbnailUrl(course.getThumbnailUrl())
                .durationMin(course.getDurationMin())
                .lectures(lectures)
                // quiz, exam, survey는 향후 추가
                .build();
    }

    /**
     * 강의별 시청률 및 완료 여부 조회
     */
    private LectureWithProgressResponse buildLectureWithProgress(Long userId, com.hr.backend.domain.course.entity.Lecture lecture) {
        // 강의 내 영상 개수
        int videoCount = lecture.getVideos().size();
        
        // 시청한 영상 개수 (예: video_watch_logs에서 완료 여부 체크)
        int completedCount = (int) lecture.getVideos().stream()
                .filter(video -> isVideoCompleted(userId, video.getVideoId()))
                .count();
        
        double watchPercentage = videoCount > 0 ? (completedCount * 100.0 / videoCount) : 0.0;
        
        // 강의 완료 여부
        boolean isCompleted = lectureProgressRepository.findByUser_UserIdAndLecture_LectureId(userId, lecture.getLectureId())
                .map(lp -> lp.isCompleted())
                .orElse(false);
        
        return LectureWithProgressResponse.builder()
                .lectureId(lecture.getLectureId())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .sortOrder(lecture.getSortOrder())
                .videoCount(videoCount)
                .completedVideoCount(completedCount)
                .watchPercentage(Math.round(watchPercentage * 10.0) / 10.0)
                .isCompleted(isCompleted)
                .build();
    }

    /**
     * 특정 영상 시청 완료 여부 확인 (예시)
     */
    private boolean isVideoCompleted(Long userId, Long videoId) {
        // TODO: video_watch_logs 테이블에서 is_completed = 1 확인
        return false; // 구현 필요
    }
}