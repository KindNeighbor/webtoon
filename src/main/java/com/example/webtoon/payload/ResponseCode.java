package com.example.webtoon.payload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    LOGIN_SUCCESS("로그인 성공"),
    CREATED_USER("회원 가입 성공"),
    GET_MY_INFO_SUCCESS("본인 정보 불러오기 성공"),
    GET_USER_INFO_SUCCESS("회원 정보 불러오기 성공");

    private final String message;
}
