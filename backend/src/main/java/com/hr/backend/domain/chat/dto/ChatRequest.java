package com.hr.backend.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequest {

    /** 사용자가 입력한 질문 메시지 */
    @NotBlank(message = "메시지는 비어있을 수 없습니다")
    @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다")
    private String message;
}
