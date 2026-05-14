package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.CourseVideoRequest;
import com.hr.backend.admin.dto.CourseVideoResponse;
import com.hr.backend.domain.course.service.CourseVideoService;
import com.hr.backend.domain.course.service.VideoUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/lectures/{lectureId}/videos")
@RequiredArgsConstructor
public class CourseVideoController {

    private final CourseVideoService courseVideoService;
    private final VideoUploadService videoUploadService;

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

    /**
     * 영상 파일 업로드 (multipart/form-data)
     * @param file      영상 파일 (mp4, webm, ogg, mov)
     * @param title     영상 제목 (생략 시 파일명 사용)
     * @param sortOrder 정렬 순서
     */
    @PostMapping("/upload")
    public ResponseEntity<CourseVideoResponse> upload(
            @PathVariable Long lectureId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "sortOrder", defaultValue = "0") int sortOrder)
            throws IOException {
        return ResponseEntity.ok(videoUploadService.upload(lectureId, file, title, sortOrder));
    }
}
