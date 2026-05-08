package com.hr.backend.admin.dto;

import com.hr.backend.domain.notice.entity.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeResponse {
    private final Long          noticeId;
    private final String        title;
    private final String        content;
    private final String        authorName;
    private final int           viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public NoticeResponse(Notice n) {
        this.noticeId   = n.getNoticeId();
        this.title      = n.getTitle();
        this.content    = n.getContent();
        this.authorName = n.getAuthor().getName();
        this.viewCount  = n.getViewCount();
        this.createdAt  = n.getCreatedAt();
        this.updatedAt  = n.getUpdatedAt();
    }
}
