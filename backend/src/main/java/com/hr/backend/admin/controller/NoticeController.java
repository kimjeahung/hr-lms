package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.NoticeRequest;
import com.hr.backend.admin.dto.NoticeResponse;
import com.hr.backend.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAll() {
        return ResponseEntity.ok(noticeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<NoticeResponse> create(
            @AuthenticationPrincipal String employeeNo,
            @RequestBody NoticeRequest req) {
        return ResponseEntity.ok(noticeService.create(employeeNo, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponse> update(
            @PathVariable Long id, @RequestBody NoticeRequest req) {
        return ResponseEntity.ok(noticeService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
