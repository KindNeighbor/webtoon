package com.example.webtoon.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    LOGIN_FAIL_EMAIL_NOT_EXIST("가입된 이메일 주소가 없습니다."),
    LOGIN_FAIL_PASSWORD_WRONG("비밀번호가 일치하지 않습니다."),
    ALREADY_EXISTED_EMAIL("중복된 이메일 주소입니다."),
    ALREADY_EXISTED_NICKNAME("중복된 닉네임입니다."),
    ALREADY_EXISTED_WEBTOON_TITLE("중복된 웹툰 제목입니다."),
    ALREADY_EXISTED_EPISODE_TITLE("중복된 에피소드 제목입니다."),
    ALREADY_RATED("이미 평가된 에피소드입니다."),
    USER_NOT_FOUND("일치하는 회원이 존재하지 않습니다."),
    WEBTOON_NOT_FOUND("일치하는 웹툰이 존재하지 않습니다."),
    EPISODE_NOT_FOUND("일치하는 에피소드가 존재하지 않습니다."),
    NICKNAME_NOT_FOUND("일치하는 닉네임이 존재하지 않습니다."),
    RATE_NOT_FOUND("일치하는 평점이 존재하지 않습니다."),
    AUTHENTICATION_NOT_MATCHED("인증정보가 일치하지 않습니다."),
    UNAUTHORIZED_USER("사용자에게 권한이 없습니다.");

    private final String message;
}
