package com.hr.backend.domain.course.service;

import com.hr.backend.admin.dto.UserVideoProgressResponse;
import com.hr.backend.admin.dto.UserVideoProgressResponse.LectureProgressDetail;
import com.hr.backend.domain.course.entity.CourseVideo;
import com.hr.backend.domain.course.entity.Lecture;
import com.hr.backend.domain.course.entity.LectureProgress;
import com.hr.backend.domain.course.repository.CourseVideoRepository;
import com.hr.backend.domain.course.repository.LectureProgressRepository;
import com.hr.backend.domain.course.repository.LectureRepository;
import com.hr.backend.domain.course.repository.VideoWatchLogRepository;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자용 직원별 영상 학습 진도 조회 서비스.
 * VideoProgressController에서 Repository를 직접 사용하던 로직을 서비스 계층으로 분리.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoProgressService {

    private final EnrollmentRepository      enrollmentRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final LectureRepository         lectureRepository;
    private final CourseVideoRepository     courseVideoRepository;
    private final VideoWatchLogRepository   videoWatchLogRepository;

    /**
     * 특정 수강 건의 강의별 진도 상세 조회.
     */
    public UserVideoProgressResponse getEnrollmentProgress(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다."));

        Long userId   = enrollment.getUser().getUserId();
        Long courseId = enrollment.getRound().getCourse().getCourseId();

        List<Lecture> lectures = lectureRepository
                .findAllByCourse_CourseIdOrderBySortOrderAsc(courseId);

        List<LectureProgress> progressList =
                lectureProgressRepository.findAllByUser_UserIdAndLecture_Course_CourseId(userId, courseId);

        Map<Long, Boolean> completedMap = new HashMap<>();
        progressList.forEach(lp -> completedMap.put(lp.getLecture().getLectureId(), lp.isCompleted()));

        List<LectureProgressDetail> details = lectures.stream().map(lecture -> {
            Long lectureId = lecture.getLectureId();
            List<CourseVideo> videos = courseVideoRepository
                    .findAllByLecture_LectureIdOrderBySortOrderAsc(lectureId);
            int totalVideos     = videos.size();
            int completedVideos = (int) videoWatchLogRepository
                    .countCompletedByUserAndLecture(userId, lectureId);

            return LectureProgressDetail.builder()
                    .lectureId(lectureId)
                    .lectureTitle(lecture.getTitle())
                    .completed(Boolean.TRUE.equals(completedMap.get(lectureId)))
                    .totalVideos(totalVideos)
                    .completedVideos(completedVideos)
                    .build();
        }).toList();

        int completedLectures = (int) details.stream()
                .filter(LectureProgressDetail::isCompleted).count();

        return UserVideoProgressResponse.builder()
                .userId(userId)
                .employeeNo(enrollment.getUser().getEmployeeNo())
                .userName(enrollment.getUser().getName())
                .departmentName(deptName(enrollment))
                .enrollmentId(enrollmentId)
                .courseTitle(enrollment.getRound().getCourse().getTitle())
                .enrollmentProgress(enrollment.getProgress())
                .lectures(details)
                .totalLectures(lectures.size())
                .completedLectures(completedLectures)
                .build();
    }

    /**
     * 특정 직원의 전체 수강 건 진도 목록.
     */
    public List<UserVideoProgressResponse> getUserProgress(Long userId) {
        return enrollmentRepository.findAllByUserId(userId).stream().map(enrollment -> {
            Long courseId = enrollment.getRound().getCourse().getCourseId();
            List<Lecture> lectures = lectureRepository
                    .findAllByCourse_CourseIdOrderBySortOrderAsc(courseId);

            long completedLectures = lectureProgressRepository
                    .findAllByUser_UserIdAndLecture_Course_CourseId(userId, courseId)
                    .stream().filter(LectureProgress::isCompleted).count();

            return UserVideoProgressResponse.builder()
                    .userId(userId)
                    .employeeNo(enrollment.getUser().getEmployeeNo())
                    .userName(enrollment.getUser().getName())
                    .departmentName(deptName(enrollment))
                    .enrollmentId(enrollment.getEnrollmentId())
                    .courseTitle(enrollment.getRound().getCourse().getTitle())
                    .enrollmentProgress(enrollment.getProgress())
                    .totalLectures(lectures.size())
                    .completedLectures((int) completedLectures)
                    .build();
        }).toList();
    }

    /**
     * 수강 건 진도 요약.
     */
    public Map<String, Object> getProgressSummary(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다."));

        Long userId   = enrollment.getUser().getUserId();
        Long courseId = enrollment.getRound().getCourse().getCourseId();

        int totalLectures = lectureRepository
                .findAllByCourse_CourseIdOrderBySortOrderAsc(courseId).size();
        long completedLectures = lectureProgressRepository
                .findAllByUser_UserIdAndLecture_Course_CourseId(userId, courseId)
                .stream().filter(LectureProgress::isCompleted).count();

        double rate = totalLectures == 0 ? 0.0
                : Math.round((completedLectures * 100.0 / totalLectures) * 10) / 10.0;

        return Map.of(
                "enrollmentId",      enrollmentId,
                "courseTitle",       enrollment.getRound().getCourse().getTitle(),
                "totalLectures",     totalLectures,
                "completedLectures", completedLectures,
                "completionRate",    rate
        );
    }

    /** 부서명 null-safe 추출 */
    private String deptName(Enrollment enrollment) {
        var dept = enrollment.getUser().getDepartment();
        return dept != null ? dept.getName() : null;
    }
}
