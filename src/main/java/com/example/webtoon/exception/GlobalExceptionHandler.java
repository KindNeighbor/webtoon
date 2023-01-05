package com.example.webtoon.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ErrorResponse.response(e.getStatusMessage(), e.getErrorCode());
    }

    // 인증 정보가 맞지 않을 때
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {

        ErrorResponse errorResponse =
            new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED,
                ErrorCode.AUTHENTICATION_NOT_MATCHED,
                ErrorCode.AUTHENTICATION_NOT_MATCHED.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // 권한이 없을 때
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {

        ErrorResponse errorResponse =
            new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN,
                ErrorCode.UNAUTHORIZED_USER,
                ErrorCode.UNAUTHORIZED_USER.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
