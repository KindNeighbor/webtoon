package com.example.webtoon.exception;

import com.example.webtoon.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomException extends RuntimeException{

    private int status;
    private HttpStatus statusMessage;
    private ErrorCode errorCode;
    private String errorMessage;

    public CustomException(HttpStatus statusMessage, ErrorCode errorCode) {
        this.status = statusMessage.value();
        this.statusMessage = statusMessage;
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
}
