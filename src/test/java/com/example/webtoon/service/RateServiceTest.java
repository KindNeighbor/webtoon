package com.example.webtoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.example.webtoon.dto.RateDto;
import com.example.webtoon.entity.Episode;
import com.example.webtoon.entity.Rate;
import com.example.webtoon.entity.User;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.EpisodeRepository;
import com.example.webtoon.repository.RateRepository;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.type.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {

    @Mock
    private RateRepository rateRepository;

    @Mock
    private EpisodeRepository episodeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RateService rateService;

    @Test
    @DisplayName("평점 등록 성공")
    void addRateSuccess() {
        // given
        Episode episode = Episode.builder()
            .episodeId(1L).build();

        User user = User.builder()
            .userId(2L).build();

        given(rateRepository.existsByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong())).willReturn(false);
        given(episodeRepository.findById(anyLong())).willReturn(Optional.of(episode));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        RateDto rateDto = rateService.addRate(episode.getEpisodeId(), user.getUserId(), 5);

        // then
        verify(rateRepository, times(1)).save(any());
        assertEquals(5, rateDto.getUserRate());
    }

    @Test
    @DisplayName("평점 등록 실패 - 이미 평가됨")
    void addRateFailed_AlreadyRated() {
        // given
        given(rateRepository.existsByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong())).willReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> rateService.addRate(1L, 2L, 5));

        // then
        assertEquals(CONFLICT, exception.getStatusMessage());
        assertEquals(ErrorCode.ALREADY_RATED, exception.getErrorCode());
    }

    @Test
    @DisplayName("평점 등록 실패 - 일치하는 에피소드 없음")
    void addRateFailed_EpisodeNotFound() {
        // given
        given(rateRepository.existsByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong())).willReturn(false);
        given(episodeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> rateService.addRate(1L, 2L, 5));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("평점 등록 실패 - 일치하는 회원 없음")
    void addRateFailed_UserNotFound() {
        // given
        Episode episode = Episode.builder()
            .episodeId(1L).build();

        given(rateRepository.existsByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong())).willReturn(false);
        given(episodeRepository.findById(anyLong())).willReturn(Optional.of(episode));
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> rateService.addRate(1L, 2L, 5));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("평점 수정 성공")
    void updateRateSuccess() {
        // given
        Episode episode = Episode.builder()
            .episodeId(1L).build();

        User user = User.builder()
            .userId(2L).build();

        Rate rate = Rate.builder()
            .rateId(1L)
            .userRate(5)
            .episode(episode)
            .user(user)
            .build();

        given(rateRepository.findByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong())).willReturn(
            Optional.of(rate));

        // when
        rateService.updateRate(1L, 2L, 4);

        // then
        verify(rateRepository, times(1)).save(any());
        assertEquals(4, rate.getUserRate());
    }

    @Test
    @DisplayName("평점 수정 실패 - 일치하는 평점 없음")
    void updateRateFailed() {
        // given
        given(rateRepository.findByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong())).willReturn(
            Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> rateService.updateRate(1L, 2L, 5));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.RATE_NOT_FOUND, exception.getErrorCode());
    }
}