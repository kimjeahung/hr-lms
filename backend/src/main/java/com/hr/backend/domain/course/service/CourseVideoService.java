package com.hr.backend.domain.course.service;

import com.hr.backend.admin.dto.CourseVideoRequest;
import com.hr.backend.admin.dto.CourseVideoResponse;
import com.hr.backend.domain.course.entity.CourseVideo;
import com.hr.backend.domain.course.entity.Lecture;
import com.hr.backend.domain.course.repository.CourseVideoRepository;
import com.hr.backend.domain.course.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseVideoService {

    private final CourseVideoRepository courseVideoRepository;
    private final LectureRepository     lectureRepository;

    /** 단원의 영상 목록 (정렬순) */
    public List<CourseVideoResponse> getAll(Long lectureId) {
        return courseVideoRepository.findAllByLecture_LectureIdOrderBySortOrderAsc(lectureId)
                .stream().map(CourseVideoResponse::new).toList();
    }

    /** 영상 단건 조회 */
    public CourseVideoResponse getOne(Long lectureId, Long videoId) {
        CourseVideo video = findById(videoId);
        validateLecture(video, lectureId);
        return new CourseVideoResponse(video);
    }

    /** 영상 등록 */
    @Transactional
    public CourseVideoResponse create(Long lectureId, CourseVideoRequest req) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("단원을 찾을 수 없습니다."));

        CourseVideo video = CourseVideo.builder()
                .lecture(lecture)
                .title(req.getTitle())
                .videoUrl(req.getVideoUrl())
                .durationSec(req.getDurationSec())
                .sortOrder(req.getSortOrder())
                .build();
        return new CourseVideoResponse(courseVideoRepository.save(video));
    }

    /** 영상 수정 */
    @Transactional
    public CourseVideoResponse update(Long lectureId, Long videoId, CourseVideoRequest req) {
        CourseVideo video = findById(videoId);
        validateLecture(video, lectureId);
        video.update(req.getTitle(), req.getVideoUrl(), req.getDurationSec(), req.getSortOrder());
        return new CourseVideoResponse(video);
    }

    /** 영상 삭제 */
    @Transactional
    public void delete(Long lectureId, Long videoId) {
        CourseVideo video = findById(videoId);
        validateLecture(video, lectureId);
        courseVideoRepository.delete(video);
    }

    private CourseVideo findById(Long videoId) {
        return courseVideoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("영상을 찾을 수 없습니다."));
    }

    private void validateLecture(CourseVideo video, Long lectureId) {
        if (!video.getLecture().getLectureId().equals(lectureId)) {
            throw new IllegalArgumentException("해당 단원의 영상이 아닙니다.");
        }
    }
}
