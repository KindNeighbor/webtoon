package com.example.webtoon.controller;


import com.example.webtoon.payload.ApiResponse;
import com.example.webtoon.payload.ResponseCode;
import com.example.webtoon.payload.UserInfo;
import com.example.webtoon.security.CurrentUser;
import com.example.webtoon.security.UserPrincipal;
import com.example.webtoon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // 본인 정보 조회
    @GetMapping("/user/my")
    public ApiResponse<?> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserInfo userInfo = userService.getCurrentUser(currentUser);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_MY_INFO_SUCCESS, userInfo);
    }

    // 회원조회(관리자)
    @GetMapping("/user/{nickname}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<?> getUserProfile(@PathVariable(value = "nickname") String nickname) {
        UserInfo userInfo = userService.getUserInfo(nickname);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_USER_INFO_SUCCESS, userInfo);
    }
}