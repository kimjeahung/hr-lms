package com.hr.backend.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        // 보안 주의: 토큰 내용(서명·페이로드)은 로그에 절대 출력하지 않음
        log.debug("[JwtFilter] {} {} | Authorization: {}",
                req.getMethod(), req.getRequestURI(),
                header != null ? "Bearer ***" : "없음");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtProvider.isValid(token)) {
                Claims claims = jwtProvider.parse(token);
                String role   = claims.get("role", String.class);
                log.debug("[JwtFilter] 토큰 유효 | subject={} role={}", claims.getSubject(), role);
                // role 이 이미 "ROLE_ADMIN" / "ROLE_USER" 형태로 저장되어 있음
                var auth = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null,
                        List.of(new SimpleGrantedAuthority(role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("[JwtFilter] 토큰 유효하지 않음 (만료 or 서명 오류) URI={}", req.getRequestURI());
            }
        } else {
            log.debug("[JwtFilter] Authorization 헤더 없거나 Bearer 형식 아님 → 인증 없이 진행");
        }
        chain.doFilter(req, res);
    }
}
