package com.hr.backend.domain.course.service;

import com.hr.backend.admin.dto.LectureRequest;
import com.hr.backend.admin.dto.LectureResponse;
import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.entity.Lecture;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.course.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;
    private final CourseRepository  courseRepository;

    /** 강좌의 단원 목록 (정렬순) */
    public List<LectureResponse> getAll(Long courseId) {
        return lectureRepository.findAllByCourse_CourseIdOrderBySortOrderAsc(courseId)
                .stream().map(LectureResponse::new).toList();
    }

    /** 단원 단건 조회 */
    public LectureResponse getOne(Long courseId, Long lectureId) {
        Lecture lecture = findById(lectureId);
        validateCourse(lecture, courseId);
        return new LectureResponse(lecture);
    }

    /** 단원 등록 */
    @Transactional
    public LectureResponse create(Long courseId, LectureRequest req) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강좌를 찾을 수 없습니다."));

        Lecture lecture = Lecture.builder()
                .course(course)
                .title(req.getTitle())
                .description(req.getDescription())
                .sortOrder(req.getSortOrder())
                .build();
        return new LectureResponse(lectureRepository.save(lecture));
    }

    /** 단원 수정 */
    @Transactional
    public LectureResponse update(Long courseId, Long lectureId, LectureRequest req) {
        Lecture lecture = findById(lectureId);
        validateCourse(lecture, courseId);
        lecture.update(req.getTitle(), req.getDescription(), req.getSortOrder());
        return new LectureResponse(lecture);
    }

    /** 단원 삭제 */
    @Transactional
    public void delete(Long courseId, Long lectureId) {
        Lecture lecture = findById(lectureId);
        validateCourse(lecture, courseId);
        lectureRepository.delete(lecture);
    }

    private Lecture findById(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("단원을 찾을 수 없습니다."));
    }

    private void validateCourse(Lecture lecture, Long courseId) {
        if (!lecture.getCourse().getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("해당 강좌의 단원이 아닙니다.");
        }
    }
}
