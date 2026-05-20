package com.hr.backend.domain.course.service;

import com.hr.backend.admin.dto.CourseRoundRequest;
import com.hr.backend.admin.dto.CourseRoundResponse;
import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.course.repository.CourseRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseRoundService {

    private final CourseRoundRepository courseRoundRepository;
    private final CourseRepository courseRepository;

    /** 강좌별 차수 목록 조회 */
    public List<CourseRoundResponse> getAll(Long courseId) {
        return courseRoundRepository.findAllByCourse_CourseIdOrderByRoundNoAsc(courseId)
                .stream()
                .map(CourseRoundResponse::new)
                .toList();
    }

    /** 차수 단건 조회 */
    public CourseRoundResponse getOne(Long courseId, Long roundId) {
        CourseRound round = findById(roundId);
        validateBelongsToCourse(round, courseId);
        return new CourseRoundResponse(round);
    }

    /** 차수 등록 */
    @Transactional
    public CourseRoundResponse create(Long courseId, CourseRoundRequest req) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강좌를 찾을 수 없습니다."));

        if (courseRoundRepository.existsByCourse_CourseIdAndRoundNo(courseId, req.getRoundNo())) {
            throw new IllegalArgumentException(req.getRoundNo() + "차수는 이미 존재합니다.");
        }

        CourseRound round = CourseRound.builder()
                .course(course)
                .roundNo(req.getRoundNo())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .build();

        return new CourseRoundResponse(courseRoundRepository.save(round));
    }

    /** 차수 수정 */
    @Transactional
    public CourseRoundResponse update(Long courseId, Long roundId, CourseRoundRequest req) {
        CourseRound round = findById(roundId);
        validateBelongsToCourse(round, courseId);
        round.update(req.getStartDate(), req.getEndDate());
        return new CourseRoundResponse(round);
    }

    /** 차수 삭제 */
    @Transactional
    public void delete(Long courseId, Long roundId) {
        CourseRound round = findById(roundId);
        validateBelongsToCourse(round, courseId);
        courseRoundRepository.delete(round);
    }

    private CourseRound findById(Long roundId) {
        return courseRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("차수를 찾을 수 없습니다."));
    }

    private void validateBelongsToCourse(CourseRound round, Long courseId) {
        if (!round.getCourse().getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("해당 강좌의 차수가 아닙니다.");
        }
    }
}
