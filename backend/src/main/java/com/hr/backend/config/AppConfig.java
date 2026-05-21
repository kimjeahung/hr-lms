package com.hr.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    /** Flask AI 서버 연결 타임아웃 (기본 5초) */
    @Value("${ai.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    /** Flask AI 서버 읽기 타임아웃 (기본 20초 — LLM 응답 시간 고려) */
    @Value("${ai.read-timeout-ms:20000}")
    private int readTimeoutMs;

    /**
     * Flask AI 서버 HTTP 통신용 RestTemplate 빈.
     * 타임아웃 미설정 시 Flask hang → Spring 스레드 무한 블로킹 방지.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .readTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }
}
