package com.hr.backend.domain.chat.controller;

import com.hr.backend.domain.chat.dto.ChatRequest;
import com.hr.backend.domain.chat.dto.ChatResponse;
import com.hr.backend.domain.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 챗봇 API.
 * POST /api/user/chat
 * Authorization: Bearer <JWT>
 * Body: { "message": "..." }
 */
@RestController
@RequestMapping("/api/user/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        // JWT 필터가 SecurityContext에 employeeNo를 principal로 저장함
        String employeeNo = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String reply = chatService.sendMessage(employeeNo, request.getMessage());

        return ResponseEntity.ok(new ChatResponse(reply, employeeNo));
    }
}
