package com.example.webtoon.service;

import com.example.webtoon.type.RoleName;
import com.example.webtoon.entity.User;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.type.ErrorCode;
import com.example.webtoon.dto.LoginRequest;
import com.example.webtoon.dto.SignUpRequest;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    public String signIn(LoginRequest loginRequest) {

        // 이메일 일치 여부
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new CustomException(
                HttpStatus.BAD_REQUEST, ErrorCode.LOGIN_FAIL_EMAIL_NOT_EXIST));

        // 비밀번호 일치 여부
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(
                HttpStatus.BAD_REQUEST, ErrorCode.LOGIN_FAIL_PASSWORD_WRONG);
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.generateToken(authentication);
    }

    // 회원가입
    public void signUp(SignUpRequest signUpRequest) {

        // 이메일 중복 여부
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new CustomException(
                HttpStatus.BAD_REQUEST, ErrorCode.ALREADY_EXIST_EMAIL);
        }

        // 닉네임 중복 여부
        if (userRepository.existsByNickname(signUpRequest.getNickname())) {
            throw new CustomException(
                HttpStatus.BAD_REQUEST, ErrorCode.ALREADY_EXIST_NICKNAME);
        }

        // 입력된 회원가입 정보 DB에 저장
        User user = new User(signUpRequest.getEmail(),
            signUpRequest.getUsername(),
            signUpRequest.getPassword(),
            signUpRequest.getNickname());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(RoleName.ROLE_USER);

        userRepository.save(user);
    }
}
