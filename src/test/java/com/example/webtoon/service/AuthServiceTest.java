package com.example.webtoon.service;

import static com.example.webtoon.type.ErrorCode.ALREADY_EXISTED_EMAIL;
import static com.example.webtoon.type.ErrorCode.ALREADY_EXISTED_NICKNAME;
import static com.example.webtoon.type.ErrorCode.LOGIN_FAIL_EMAIL_NOT_EXIST;
import static com.example.webtoon.type.ErrorCode.LOGIN_FAIL_PASSWORD_WRONG;
import static com.example.webtoon.type.RoleName.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.example.webtoon.dto.LoginRequest;
import com.example.webtoon.dto.SignUpRequest;
import com.example.webtoon.entity.User;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Spy
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("로그인 성공")
    void signInSuccess() {

        // given
        User user = User.builder()
            .userId(10L)
            .email("test@test.com")
            .username("testUserName1")
            .password(passwordEncoder.encode("testPassword"))
            .nickname("testNickName1")
            .role(ROLE_USER)
            .build();

        LoginRequest loginRequest = LoginRequest.builder()
            .email("test@test.com")
            .password("testPassword")
            .build();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // when
        User result = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

        boolean passwordCheck =
            passwordEncoder.matches(loginRequest.getPassword(), result.getPassword());

        // then
        assertEquals(loginRequest.getEmail(), result.getEmail());
        assertTrue(passwordCheck);
    }

    @Test
    @DisplayName("로그인 실패 - 입력한 이메일과 일치하는 정보 없음")
    void signInFailed_EmailNotFound() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
            .email("test@test.com")
            .password("testPassword")
            .build();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signIn(loginRequest));

        // then
        assertEquals(BAD_REQUEST, exception.getStatusMessage());
        assertEquals(LOGIN_FAIL_EMAIL_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void signInFailed_WrongPassword() {
        // given
        User user = User.builder()
            .userId(10L)
            .email("test@test.com")
            .username("testUserName")
            .password(passwordEncoder.encode("testPassword"))
            .nickname("testNickName")
            .role(ROLE_USER)
            .build();

        LoginRequest loginRequest = LoginRequest.builder()
            .email("test@test.com")
            .password("WrongPassword")
            .build();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signIn(loginRequest));

        // then
        assertEquals(BAD_REQUEST, exception.getStatusMessage());
        assertEquals(LOGIN_FAIL_PASSWORD_WRONG, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .email("test@test.com")
            .username("testUsername")
            .password("testPassword")
            .nickname("testNickName")
            .build();

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByNickname(anyString())).willReturn(false);

        // when
        authService.signUp(signUpRequest);

        // then
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일 존재")
    void signUpFailed_AlreadyExistEmail() {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .email("test@test.com")
            .username("testUsername")
            .password("testPassword")
            .nickname("testNickName")
            .build();

        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signUp(signUpRequest));

        // then
        assertEquals(BAD_REQUEST, exception.getStatusMessage());
        assertEquals(ALREADY_EXISTED_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 닉네임 존재")
    void signUpFailed_AlreadyExistNickname() {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .email("test@test.com")
            .username("testUsername")
            .password("testPassword")
            .nickname("testNickName")
            .build();

        given(userRepository.existsByNickname(anyString())).willReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signUp(signUpRequest));

        // then
        assertEquals(BAD_REQUEST, exception.getStatusMessage());
        assertEquals(ALREADY_EXISTED_NICKNAME, exception.getErrorCode());
    }
}