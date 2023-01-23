package com.example.webtoon.controller;

import com.example.webtoon.dto.ApiResponse;
import com.example.webtoon.dto.LoginRequest;
import com.example.webtoon.dto.SignUpRequest;
import com.example.webtoon.dto.TokenResponse;
import com.example.webtoon.service.AuthService;
import com.example.webtoon.type.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Api(tags = {"로그인, 회원가입 컨트롤러"})
public class AuthController {

    private final AuthService authService;

    // 로그인
    @ApiOperation("로그인")
    @PostMapping("/signin")
    public ApiResponse<TokenResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        String jwt = authService.signIn(loginRequest);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.LOGIN_SUCCESS, new TokenResponse(jwt));
    }

    // 회원가입
    @ApiOperation("회원가입")
    @PostMapping("/signup")
    public ApiResponse<SignUpRequest> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        authService.signUp(signUpRequest);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.CREATED_USER, signUpRequest);
    }
}