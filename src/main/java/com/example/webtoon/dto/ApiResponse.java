package com.example.webtoon.dto;

import com.example.webtoon.type.ResponseCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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