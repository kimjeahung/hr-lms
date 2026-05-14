package com.hr.backend.domain.course.repository;

import com.hr.backend.domain.course.entity.VideoWatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoWatchLogRepository extends JpaRepository<VideoWatchLog, Long> {

    Optional<VideoWatchLog> findByUser_UserIdAndVideo_VideoId(Long userId, Long videoId);
}
