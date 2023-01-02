package com.example.webtoon.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private int statusCode;
    private String responseMessage;
    private T data;

    public ApiResponse(final int statusCode, final String responseMessage) {
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
        this.data = null;
    }

    public static<T> ApiResponse<T> res(final int statusCode, final String responseMessage) {
        return res(statusCode, responseMessage, null);
    }

    public static<T> ApiResponse<T> res(final int statusCode, final String responseMessage, final T t) {
        return ApiResponse.<T>builder()
            .data(t)
            .statusCode(statusCode)
            .responseMessage(responseMessage)
            .build();
    }
}