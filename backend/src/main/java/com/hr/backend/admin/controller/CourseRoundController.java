package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.CourseRoundRequest;
import com.hr.backend.admin.dto.CourseRoundResponse;
import com.hr.backend.domain.course.service.CourseRoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses/{courseId}/rounds")
@RequiredArgsConstructor
public class CourseRoundController {

    private final CourseRoundService courseRoundService;

    /** 강좌별 차수 목록 조회 */
    @GetMapping
    public ResponseEntity<List<CourseRoundResponse>> getAll(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseRoundService.getAll(courseId));
    }

    /** 차수 단건 조회 */
    @GetMapping("/{roundId}")
    public ResponseEntity<CourseRoundResponse> getOne(
            @PathVariable Long courseId, @PathVariable Long roundId) {
        return ResponseEntity.ok(courseRoundService.getOne(courseId, roundId));
    }

    /** 차수 등록 */
    @PostMapping
    public ResponseEntity<CourseRoundResponse> create(
            @PathVariable Long courseId, @RequestBody CourseRoundRequest req) {
        return ResponseEntity.ok(courseRoundService.create(courseId, req));
    }

    /** 차수 수정 (시작일/마감일만 변경 가능) */
    @PutMapping("/{roundId}")
    public ResponseEntity<CourseRoundResponse> update(
            @PathVariable Long courseId,
            @PathVariable Long roundId,
            @RequestBody CourseRoundRequest req) {
        return ResponseEntity.ok(courseRoundService.update(courseId, roundId, req));
    }

    /** 차수 삭제 */
    @DeleteMapping("/{roundId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long courseId, @PathVariable Long roundId) {
        courseRoundService.delete(courseId, roundId);
        return ResponseEntity.noContent().build();
    }
}
