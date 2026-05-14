package com.hr.backend.domain.course.repository;

import com.hr.backend.domain.course.entity.CourseVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseVideoRepository extends JpaRepository<CourseVideo, Long> {

    List<CourseVideo> findAllByLecture_LectureIdOrderBySortOrderAsc(Long lectureId);
}
