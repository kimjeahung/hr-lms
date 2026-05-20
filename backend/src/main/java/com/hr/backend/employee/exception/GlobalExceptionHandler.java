package com.hr.backend.employee.exception;

import com.hr.backend.employee.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(CommonResponse.fail(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CommonResponse<?>> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(CommonResponse.fail(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CommonResponse<?>> handleForbiddenException(ForbiddenException ex) {
        return new ResponseEntity<>(CommonResponse.fail(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<CommonResponse<?>> handleAlreadyExistsException(AlreadyExistsException ex) {
        return new ResponseEntity<>(CommonResponse.fail(ex.getMessage()), HttpStatus.CONFLICT);
    }

    // 비즈니스 로직 검증 실패 (로그인 실패, 중복 등) - 클라이언트에 메시지 노출 허용
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(CommonResponse.fail(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(CommonResponse.fail("입력값 검증 실패", errors), HttpStatus.BAD_REQUEST);
    }

    // 예상치 못한 서버 오류 - 내부 정보를 클라이언트에 노출하지 않음
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleGlobalException(Exception ex) {
        log.error("[서버 내부 오류] {}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(CommonResponse.fail("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}