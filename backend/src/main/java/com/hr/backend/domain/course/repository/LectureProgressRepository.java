package com.hr.backend.domain.course.repository;

import com.hr.backend.domain.course.entity.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureProgressRepository extends JpaRepository<LectureProgress, Long> {

    Optional<LectureProgress> findByUser_UserIdAndLecture_LectureId(Long userId, Long lectureId);

    // 강좌 내 전체 강의 완료 여부 확인용 (courseId 기반)
    List<LectureProgress> findAllByUser_UserIdAndLecture_Course_CourseId(Long userId, Long courseId);

    // 특정 사용자가 특정 강좌(course)에 속한 강의들 중
    // 완료 처리된 강의 수를 조회    //
    // 조건:
    // - user.userId = userId
    // - lecture.course.courseId = courseId
    // - completed = true    //
    // 사용 위치:
    // - 강좌 진도율 계산
    // - 수강 완료 여부 판단
    // - 이수증 발급 조건 확인    //
    // 예:
    // 전체 강의 10개 중 이 메서드 결과가 10이면
    // 해당 사용자는 해당 강좌의 모든 강의를 완료한 것으로 판단 가능
    long countByUser_UserIdAndLecture_Course_CourseIdAndCompletedTrue(
            Long userId,
            Long courseId
    );
}
