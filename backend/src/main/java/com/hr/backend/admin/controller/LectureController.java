package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.LectureRequest;
import com.hr.backend.admin.dto.LectureResponse;
import com.hr.backend.domain.course.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses/{courseId}/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    /** 강좌의 단원 목록 */
    @GetMapping
    public ResponseEntity<List<LectureResponse>> getAll(@PathVariable Long courseId) {
        return ResponseEntity.ok(lectureService.getAll(courseId));
    }

    /** 단원 단건 조회 */
    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureResponse> getOne(
            @PathVariable Long courseId, @PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getOne(courseId, lectureId));
    }

    /** 단원 등록 */
    @PostMapping
    public ResponseEntity<LectureResponse> create(
            @PathVariable Long courseId, @RequestBody LectureRequest req) {
        return ResponseEntity.ok(lectureService.create(courseId, req));
    }

    /** 단원 수정 */
    @PutMapping("/{lectureId}")
    public ResponseEntity<LectureResponse> update(
            @PathVariable Long courseId,
            @PathVariable Long lectureId,
            @RequestBody LectureRequest req) {
        return ResponseEntity.ok(lectureService.update(courseId, lectureId, req));
    }

    /** 단원 삭제 */
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long courseId, @PathVariable Long lectureId) {
        lectureService.delete(courseId, lectureId);
        return ResponseEntity.noContent().build();
    }
}
