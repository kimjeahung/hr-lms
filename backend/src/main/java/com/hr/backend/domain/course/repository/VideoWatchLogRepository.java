package com.hr.backend.domain.course.repository;

import com.hr.backend.domain.course.entity.VideoWatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VideoWatchLogRepository extends JpaRepository<VideoWatchLog, Long> {

    Optional<VideoWatchLog> findByUser_UserIdAndVideo_VideoId(Long userId, Long videoId);

    @Query("SELECT COUNT(w) FROM VideoWatchLog w WHERE w.user.userId = :userId AND w.video.lecture.lectureId = :lectureId AND w.completed = true")
    long countCompletedByUserAndLecture(@Param("userId") Long userId, @Param("lectureId") Long lectureId);
}
