package com.hr.backend.admin.controller;

import com.hr.backend.domain.notification.dto.NotificationResponse;
import com.hr.backend.domain.notification.entity.Notification.NotificationType;
import com.hr.backend.domain.notification.service.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 관리자용 알림 관리 API.
 *
 * GET  /api/admin/notifications              — 전체 알림 이력 (type 필터 가능)
 * POST /api/admin/notifications/broadcast    — 공지 알림 발송 (전체 or 특정 직원)
 */
@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final NotificationService notificationService;

    /**
     * 전체 알림 이력 조회.
     * @param type 알림 유형 필터 (선택): ENROLLMENT_APPROVED, ENROLLMENT_REJECTED,
     *             COURSE_STARTED, COURSE_DEADLINE, CERTIFICATE_ISSUED, SYSTEM
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll(
            @RequestParam(required = false) String type) {

        if (type != null && !type.isBlank()) {
            NotificationType nType = NotificationType.valueOf(type.toUpperCase());
            return ResponseEntity.ok(notificationService.getNotificationsByType(nType));
        }
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    /**
     * 공지 알림 브로드캐스트.
     * userIds 생략 시 전체 재직 직원에게 발송.
     * Body: { "message": "...", "userIds": [1, 2, 3] }  // userIds 생략 가능
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>> broadcast(
            @RequestBody BroadcastRequest request) {

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("알림 메시지는 비어있을 수 없습니다.");
        }

        int count = notificationService.broadcast(request.getMessage(), request.getUserIds());
        return ResponseEntity.ok(Map.of(
                "message", "알림 발송 완료",
                "sentCount", count
        ));
    }

    @Getter
    @Setter
    static class BroadcastRequest {
        private String     message;
        private List<Long> userIds; // null 또는 빈 리스트 → 전체 발송
    }
}
