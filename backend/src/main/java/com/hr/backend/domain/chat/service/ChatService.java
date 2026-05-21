package com.hr.backend.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Flask AI 챗봇 서버(/chat)와 통신하는 서비스.
 * application.yaml: ai.server.url (기본값 http://localhost:5000)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url:http://localhost:5000}")
    private String aiServerUrl;

    /**
     * Flask 챗봇에 메시지를 전달하고 응답을 받는다.
     *
     * @param employeeNo JWT에서 추출한 사번 (Flask 측 user_id로 전달 → 대화 이력 유지)
     * @param message    사용자 질문
     * @return AI 응답 문자열
     */
    public String sendMessage(String employeeNo, String message) {
        String url = aiServerUrl + "/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Flask가 기대하는 요청 구조: { "user_id": "...", "message": "..." }
        Map<String, String> body = Map.of(
                "user_id", employeeNo,
                "message", message
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object reply = response.getBody().get("reply");
                return reply != null ? reply.toString() : "AI 서버에서 응답을 받지 못했습니다.";
            }

            log.warn("AI 서버 비정상 응답: status={}", response.getStatusCode());
            return "AI 서버에서 응답을 받지 못했습니다.";

        } catch (RestClientException e) {
            log.error("AI 서버 연결 실패: url={}, error={}", url, e.getMessage());
            return "챗봇 서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.";
        }
    }
}
