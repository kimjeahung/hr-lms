package com.hr.backend.employee.service;

import com.hr.backend.domain.notice.entity.Notice;
import com.hr.backend.domain.notice.repository.NoticeRepository;
import com.hr.backend.employee.dto.response.NoticeResponse;
import com.hr.backend.employee.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeNoticeService {
    private final NoticeRepository noticeRepository;
    public Page<NoticeResponse.NoticeListItem> getAllNotices(Pageable pageable) {
        List<NoticeResponse.NoticeListItem> list = noticeRepository.findAllWithAuthor().stream().map(n -> NoticeResponse.NoticeListItem.builder()
                .noticeId(n.getNoticeId()).title(n.getTitle()).contentPreview(preview(n.getContent()))
                .viewCount(n.getViewCount()).isPinned(false).createdAt(n.getCreatedAt()).build()).toList();
        int start=(int)Math.min(pageable.getOffset(), list.size()); int end=Math.min(start+pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start,end), pageable, list.size());
    }
    @Transactional
    public NoticeResponse getNoticeDetail(Long noticeId) {
        Notice n = noticeRepository.findByIdWithAuthor(noticeId).orElseThrow(() -> new ResourceNotFoundException("Notice", "noticeId", noticeId));
        n.incrementViewCount();
        return NoticeResponse.builder().noticeId(n.getNoticeId()).title(n.getTitle()).content(n.getContent())
                .viewCount(n.getViewCount()).isPinned(false).createdAt(n.getCreatedAt()).updatedAt(n.getUpdatedAt()).build();
    }
    private String preview(String s){if(s==null)return ""; return s.length()>80?s.substring(0,80)+"...":s;}
}
