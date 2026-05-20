package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.dto.request.VideoWatchLogRequest;
import com.hr.backend.employee.dto.response.VideoResponse;
import com.hr.backend.employee.service.EmployeeVideoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmployeeVideoController {

    private final EmployeeVideoService videoService;

    @GetMapping("/courses/{courseId}/videos")
    public ResponseEntity<CommonResponse<VideoResponse.VideoListResponse>> getCourseVideos(@PathVariable Long courseId) {
        VideoResponse.VideoListResponse videos = videoService.getCourseVideos(courseId);
        return ResponseEntity.ok(CommonResponse.success("특정 강의의 영상 목록을 성공적으로 조회했습니다.", videos));
    }

    @PostMapping("/videos/{videoId}/watch")
    public ResponseEntity<CommonResponse<?>> recordVideoWatchLog(
            @PathVariable Long videoId,
            @Valid @RequestBody VideoWatchLogRequest request
    ) {
        videoService.recordVideoWatchLog(videoId, request);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("영상 시청 기록이 업데이트되었습니다."));
    }
}