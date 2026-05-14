package com.hr.backend.domain.course.repository;

import com.hr.backend.domain.course.entity.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureProgressRepository extends JpaRepository<LectureProgress, Long> {

    Optional<LectureProgress> findByUser_UserIdAndLecture_LectureId(Long userId, Long lectureId);

    // 강좌 내 전체 강의 완료 여부 확인용 (courseId 기반)
    List<LectureProgress> findAllByUser_UserIdAndLecture_Course_CourseId(Long userId, Long courseId);
}
