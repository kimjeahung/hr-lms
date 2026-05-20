package com.hr.backend.employee.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NoticeResponse {
    private Long noticeId;
    private String title;
    private String content;
    private Integer viewCount;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 공지사항 목록 조회 시 사용
    @Getter
    @Setter
    @Builder
    public static class NoticeListItem {
        private Long noticeId;
        private String title;
        private String contentPreview; // 내용 미리보기
        private Integer viewCount;
        private Boolean isPinned;
        private LocalDateTime createdAt;
    }
}