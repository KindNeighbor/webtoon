package com.example.webtoon.exception;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
@ToString
public class ErrorResponse {

    private int status;
    private HttpStatus statusMessage;
    private ErrorCode errorCode;
    private String errorMessage;

    public static ResponseEntity<ErrorResponse> response(HttpStatus statusMessage, ErrorCode errorCode) {

        return ResponseEntity.status(statusMessage).body(
            ErrorResponse.builder()
                .status(statusMessage.value())
                .statusMessage(statusMessage)
                .errorCode(errorCode)
                .errorMessage(errorCode.getMessage())
                .build()
            );
    }
}
