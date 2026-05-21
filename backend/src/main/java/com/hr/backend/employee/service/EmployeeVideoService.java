package com.hr.backend.employee.service;

import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.entity.CourseVideo;
import com.hr.backend.domain.course.entity.Lecture;
import com.hr.backend.domain.course.entity.LectureProgress;
import com.hr.backend.domain.course.entity.VideoWatchLog;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.course.repository.CourseVideoRepository;
import com.hr.backend.domain.course.repository.LectureProgressRepository;
import com.hr.backend.domain.course.repository.VideoWatchLogRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.employee.dto.request.VideoWatchLogRequest;
import com.hr.backend.employee.dto.response.VideoResponse;
import com.hr.backend.employee.exception.ResourceNotFoundException;
import com.hr.backend.employee.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeVideoService {
    private final CourseRepository courseRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final VideoWatchLogRepository videoWatchLogRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final CurrentUserProvider currentUserProvider;
    private final EmployeeLearningCompletionService completionService;

    public VideoResponse.VideoListResponse getCourseVideos(Long courseId) {
        User user = currentUserProvider.getCurrentUser();
        Course c = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course", "courseId", courseId));
        List<VideoResponse> videos = c.getLectures().stream().flatMap(l -> l.getVideos().stream())
                .sorted(Comparator.comparing((CourseVideo v) -> v.getLecture().getSortOrder()).thenComparingInt(CourseVideo::getSortOrder))
                .map(v -> {
                    VideoWatchLog log = videoWatchLogRepository.findByUser_UserIdAndVideo_VideoId(user.getUserId(), v.getVideoId()).orElse(null);
                    return VideoResponse.builder().videoId(v.getVideoId()).title(v.getTitle()).videoURL(v.getVideoUrl())
                            .durationSec(v.getDurationSec()).sortOrder(v.getSortOrder())
                            .watchedSec(log == null ? 0 : log.getWatchedSec()).isCompleted(log != null && log.isCompleted()).build();
                }).toList();
        return VideoResponse.VideoListResponse.builder().courseId(c.getCourseId()).courseTitle(c.getTitle()).videos(videos).build();
    }

    @Transactional
    public void recordVideoWatchLog(Long videoId, VideoWatchLogRequest request) {
        User user = currentUserProvider.getCurrentUser();
        CourseVideo video = courseVideoRepository.findById(videoId).orElseThrow(() -> new ResourceNotFoundException("Video", "videoId", videoId));
        VideoWatchLog log = videoWatchLogRepository.findByUser_UserIdAndVideo_VideoId(user.getUserId(), videoId)
                .orElseGet(() -> VideoWatchLog.builder().user(user).video(video).build());
        log.endSession(request.getWatchedSec());
        videoWatchLogRepository.save(log);
        if (log.isCompleted()) {
            Lecture lecture = video.getLecture();
            boolean allVideosCompleted = lecture.getVideos().stream().allMatch(v ->
                    v.getVideoId().equals(videoId) || videoWatchLogRepository.findByUser_UserIdAndVideo_VideoId(user.getUserId(), v.getVideoId()).map(VideoWatchLog::isCompleted).orElse(false));
            if (allVideosCompleted) {
                LectureProgress progress = lectureProgressRepository.findByUser_UserIdAndLecture_LectureId(user.getUserId(), lecture.getLectureId())
                        .orElseGet(() -> LectureProgress.builder().user(user).lecture(lecture).build());
                progress.complete();
                lectureProgressRepository.save(progress);
                completionService.recalculateEnrollmentProgress(user, lecture.getCourse());
            }
        }
    }
}
