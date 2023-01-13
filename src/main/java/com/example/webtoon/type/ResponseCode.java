package com.example.webtoon.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    LOGIN_SUCCESS("로그인 성공"),
    CREATED_USER("회원 가입 성공"),
    GET_MY_INFO_SUCCESS("본인 정보 불러오기 성공"),
    GET_USER_INFO_SUCCESS("회원 정보 불러오기 성공"),
    GET_RATED_WEBTOON_LIST_SUCCESS("평점 부여한 웹툰 목록 불러오기 성공"),
    CREATE_NEW_WEBTOON("신규 웹툰 등록 성공"),
    UPDATE_WEBTOON_SUCCESS("웹툰 수정 성공"),
    DELETE_WEBTOON_SUCCESS("웹툰 삭제 성공"),
    GET_WEBTOON_AVG_RAGE_SUCCESS("웹툰 평균 평점 불러오기 성공"),
    GET_WEBTOON_BY_DAY_SUCCESS("요일별 웹툰 불러오기 성공"),
    CREATE_NEW_EPISODE("신규 에피소드 등록 성공"),
    UPDATE_EPISODE_SUCCESS("에피소드 수정 성공"),
    DELETE_EPISODE_SUCCESS("에피소드 삭제 성공"),
    GET_EPISODE_AVG_RAGE_SUCCESS("웹툰 평균 평점 불러오기 성공"),
    GET_EPISODES_SUCCESS("에피소드 목록 불러오기 성공"),
    CREATE_RATE_SUCCESS("평점 부여 성공"),
    UPDATE_RATE_SUCCESS("평점 수정 성공"),
    DELETE_RATE_SUCCESS("평점 삭제 성공"),
    ADD_FAV_WEBTOON_SUCCESS("선호 작품 등록 성공"),
    GET_FAV_WEBTOON_LIST_SUCCESS("선호 작품 목록 조회 성공"),
    DELETE_FAV_WEBTOON_SUCCESS("선호 작품 삭제 성공");


    private final String message;
}
