package com.example.webtoon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.example.webtoon.dto.CommentDto;
import com.example.webtoon.dto.WebtoonIdListDto;
import com.example.webtoon.entity.Comment;
import com.example.webtoon.entity.Episode;
import com.example.webtoon.entity.User;
import com.example.webtoon.entity.Webtoon;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.EpisodeRepository;
import com.example.webtoon.repository.FavRepository;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.type.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FavServiceTest {

    @Mock
    private WebtoonRepository webtoonRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FavRepository favRepository;
    @InjectMocks
    private FavService favService;

    @Test
    @DisplayName("선호 작품 등록 성공")
    void addFavWebtoonSuccess() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .build();

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.of(webtoon));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(favRepository.existsByWebtoon_WebtoonIdAndUser_UserId(anyLong(), anyLong())).willReturn(false);

        // when
        WebtoonIdListDto webtoonIdListDto =
            favService.addFavWebtoon(webtoon.getWebtoonId(), user.getUserId());

        // then
        verify(favRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("선호 작품 등록 실패 - 일치하는 웹툰 없음")
    void addFavWebtoonFailed_WebtoonNotFound() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .build();

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> favService.addFavWebtoon(webtoon.getWebtoonId(), user.getUserId()));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.WEBTOON_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("선호 작품 등록 실패 - 일치하는 회원 없음")
    void addFavWebtoonFailed_UserNotFound() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .build();

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.of(webtoon));
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> favService.addFavWebtoon(webtoon.getWebtoonId(), user.getUserId()));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("선호 작품 등록 실패 - 이미 등록한 작품")
    void addFavWebtoonFailed_AlreadyExistFavWebtoon() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .build();

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.of(webtoon));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(favRepository.existsByWebtoon_WebtoonIdAndUser_UserId(anyLong(), anyLong())).willReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> favService.addFavWebtoon(webtoon.getWebtoonId(), user.getUserId()));

        // then
        assertEquals(CONFLICT, exception.getStatusMessage());
        assertEquals(ErrorCode.ALREADY_EXIST_FAV_WEBTOON, exception.getErrorCode());
    }

    @Test
    @DisplayName("선호 작품 취소 성공")
    void deleteFavWebtoonSuccess() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .build();

        given(favRepository.existsByWebtoon_WebtoonIdAndUser_UserId(anyLong(), anyLong())).willReturn(true);

        // when
        favService.deleteFavWebtoon(webtoon.getWebtoonId(), user.getUserId());

        // then
        verify(favRepository, times(1))
            .existsByWebtoon_WebtoonIdAndUser_UserId(anyLong(), anyLong());
        verify(favRepository, times(1))
            .deleteByWebtoon_WebtoonIdAndUser_UserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("선호 작품 취소 실패 - 일치하는 선호작품 없음")
    void addFavWebtoonFailed_FavWebtoonNotFound() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .build();

        given(favRepository.existsByWebtoon_WebtoonIdAndUser_UserId(anyLong(), anyLong())).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> favService.deleteFavWebtoon(webtoon.getWebtoonId(), user.getUserId()));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.FAV_WEBTOON_NOT_FOUND, exception.getErrorCode());
    }
}