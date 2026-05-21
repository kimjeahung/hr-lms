package com.hr.backend.domain.notification.repository;

import com.hr.backend.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** 사용자의 전체 알림 (최신순) */
    List<Notification> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);

    /** 사용자의 읽지 않은 알림만 */
    List<Notification> findAllByUser_UserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    /** 읽지 않은 알림 개수 */
    long countByUser_UserIdAndIsReadFalse(Long userId);

    /** 특정 사용자의 모든 알림 일괄 읽음 처리 */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.userId = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}
