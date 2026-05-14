package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.CourseVideoRequest;
import com.hr.backend.admin.dto.CourseVideoResponse;
import com.hr.backend.domain.course.service.CourseVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lectures/{lectureId}/videos")
@RequiredArgsConstructor
public class CourseVideoController {

    private final CourseVideoService courseVideoService;

    /** 단원의 영상 목록 */
    @GetMapping
    public ResponseEntity<List<CourseVideoResponse>> getAll(@PathVariable Long lectureId) {
        return ResponseEntity.ok(courseVideoService.getAll(lectureId));
    }

    /** 영상 단건 조회 */
    @GetMapping("/{videoId}")
    public ResponseEntity<CourseVideoResponse> getOne(
            @PathVariable Long lectureId, @PathVariable Long videoId) {
        return ResponseEntity.ok(courseVideoService.getOne(lectureId, videoId));
    }

    /** 영상 등록 */
    @PostMapping
    public ResponseEntity<CourseVideoResponse> create(
            @PathVariable Long lectureId, @RequestBody CourseVideoRequest req) {
        return ResponseEntity.ok(courseVideoService.create(lectureId, req));
    }

    /** 영상 수정 */
    @PutMapping("/{videoId}")
    public ResponseEntity<CourseVideoResponse> update(
            @PathVariable Long lectureId,
            @PathVariable Long videoId,
            @RequestBody CourseVideoRequest req) {
        return ResponseEntity.ok(courseVideoService.update(lectureId, videoId, req));
    }

    /** 영상 삭제 */
    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long lectureId, @PathVariable Long videoId) {
        courseVideoService.delete(lectureId, videoId);
        return ResponseEntity.noContent().build();
    }
}
