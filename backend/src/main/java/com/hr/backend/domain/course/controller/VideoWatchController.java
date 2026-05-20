package com.hr.backend.domain.course.controller;

import com.hr.backend.domain.course.service.VideoUploadService;
import com.hr.backend.domain.course.service.VideoWatchLogService;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class VideoWatchController {

    private final VideoWatchLogService videoWatchLogService;
    private final VideoUploadService   videoUploadService;
    private final UserRepository       userRepository;

    // ── 스트리밍 ────────────────────────────────────────────

    /**
     * 영상 스트리밍
     * GET /api/user/videos/{lectureId}/stream/{filename}
     * videoUrl에 저장된 경로 그대로 호출
     */
    @GetMapping("/videos/{lectureId}/stream/{filename}")
    public ResponseEntity<Resource> stream(
            @PathVariable Long lectureId,
            @PathVariable String filename) {

        Resource resource    = videoUploadService.stream(lectureId, filename);
        String   contentType = videoUploadService.detectContentType(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    // ── 시청 로그 ───────────────────────────────────────────

    /** 영상 시청 시작 */
    @PostMapping("/videos/{videoId}/watch/start")
    public ResponseEntity<Void> startWatch(@PathVariable Long videoId) {
        videoWatchLogService.startWatch(getLoginUserId(), videoId);
        return ResponseEntity.ok().build();
    }

    /**
     * 영상 시청 종료
     * @param watchedSec 이번 세션에서 실제 시청한 초
     */
    @PostMapping("/videos/{videoId}/watch/end")
    public ResponseEntity<Map<String, Object>> endWatch(
            @PathVariable Long videoId,
            @RequestParam int watchedSec) {
        return ResponseEntity.ok(
                videoWatchLogService.endWatch(getLoginUserId(), videoId, watchedSec)
        );
    }

    /** 강의 영상별 시청 완료 현황 조회 */
    @GetMapping("/lectures/{lectureId}/watch-status")
    public ResponseEntity<List<Map<String, Object>>> getLectureWatchStatus(
            @PathVariable Long lectureId) {
        return ResponseEntity.ok(
                videoWatchLogService.getLectureWatchStatus(getLoginUserId(), lectureId)
        );
    }

    private Long getLoginUserId() {
        String employeeNo = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."))
                .getUserId();
    }
}
