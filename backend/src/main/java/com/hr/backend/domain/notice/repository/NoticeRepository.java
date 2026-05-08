package com.hr.backend.domain.notice.repository;

import com.hr.backend.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n JOIN FETCH n.author ORDER BY n.createdAt DESC")
    List<Notice> findAllWithAuthor();

    @Query("SELECT n FROM Notice n JOIN FETCH n.author WHERE n.noticeId = :id")
    Optional<Notice> findByIdWithAuthor(Long id);
}
