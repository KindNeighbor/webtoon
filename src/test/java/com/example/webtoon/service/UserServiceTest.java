package com.example.webtoon.service;

import static com.example.webtoon.type.RoleName.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.example.webtoon.dto.UserInfo;
import com.example.webtoon.entity.User;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.type.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Spy
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 조회 성공")
    void getUserInfoSuccess() {

        // given
        User user = User.builder()
            .userId(10L)
            .email("test@test.com")
            .username("testUserName")
            .password(passwordEncoder.encode("testPassword"))
            .nickname("testNickName")
            .role(ROLE_USER)
            .build();

        given(userRepository.findByNickname(anyString())).willReturn(Optional.of(user));
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // when
        UserInfo userInfo = userService.getUserInfo("testNickName");

        // then
        verify(userRepository, times(1)).findByNickname(captor.capture());
        assertEquals(userInfo.getNickname(), captor.getValue());
    }

    @Test
    @DisplayName("회원 조회 실패 - 입력한 닉네임과 일치하는 정보 없음")
    void getUserInfoFailed_NicknameNotFound() {

        // given
        given(userRepository.findByNickname(anyString())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.getUserInfo("testNickName"));

        // then
        assertEquals(BAD_REQUEST, exception.getStatusMessage());
        assertEquals(ErrorCode.NICKNAME_NOT_FOUND, exception.getErrorCode());
    }
}