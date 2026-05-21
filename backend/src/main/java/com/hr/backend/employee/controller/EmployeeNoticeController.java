package com.hr.backend.employee.controller;

import com.hr.backend.employee.dto.CommonResponse;
import com.hr.backend.employee.dto.response.NoticeResponse;
import com.hr.backend.employee.service.EmployeeNoticeService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/notices")
@RequiredArgsConstructor
public class EmployeeNoticeController {

    private final EmployeeNoticeService noticeService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<NoticeResponse.NoticeListItem>>> getAllNotices(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        // is_pinned 우선 정렬, created_at 최신순 정렬은 Service에서 처리
        Pageable pageable = PageRequest.of(page, size); // 정렬은 Service에서 구현된 Repository 메서드에 맡김
        Page<NoticeResponse.NoticeListItem> notices = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(CommonResponse.success("공지사항 목록을 성공적으로 조회했습니다.", notices));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<CommonResponse<NoticeResponse>> getNoticeDetail(@PathVariable Long noticeId) {
        NoticeResponse noticeDetail = noticeService.getNoticeDetail(noticeId);
        return ResponseEntity.ok(CommonResponse.success("공지사항 상세 정보를 성공적으로 조회했습니다.", noticeDetail));
    }
}