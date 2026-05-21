package com.hr.backend.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatResponse {

    /** Flask AI 서버가 돌려준 응답 메시지 */
    private String reply;

    /** 요청한 사용자 ID (employeeNo) */
    private String userId;
}
