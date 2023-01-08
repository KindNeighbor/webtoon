package com.example.webtoon.dto;

import com.example.webtoon.type.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private int status;
    private HttpStatus statusMessage;
    private ResponseCode responseCode;
    private String responseMessage;
    @Nullable
    private T data;

    public ApiResponse(HttpStatus statusMessage, ResponseCode responseCode) {
        this.status = statusMessage.value();
        this.statusMessage = statusMessage;
        this.responseCode = responseCode;
        this.responseMessage = responseCode.getMessage();
    }

    public ApiResponse(HttpStatus statusMessage, ResponseCode responseCode, @Nullable T data) {
        this.status = statusMessage.value();
        this.statusMessage = statusMessage;
        this.responseCode = responseCode;
        this.responseMessage = responseCode.getMessage();
        this.data = data;
    }
}