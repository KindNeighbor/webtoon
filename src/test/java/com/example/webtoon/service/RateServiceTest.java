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

import com.example.webtoon.dto.RateAvgDto;
import com.example.webtoon.dto.RateDto;
import com.example.webtoon.entity.Episode;
import com.example.webtoon.entity.Rate;
import com.example.webtoon.entity.User;
import com.example.webtoon.entity.Webtoon;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.EpisodeRepository;
import com.example.webtoon.repository.RateRepository;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.type.ErrorCode;
import java.util.Arrays;
import java.util.List;
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
    @Mock
    private WebtoonRepository webtoonRepository;
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

    @Test
    @DisplayName("평점 삭제 성공")
    void deleteRateSuccess() {

        // given
        given(rateRepository.existsByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong())).willReturn(true);

        // when
        rateService.deleteRate(1L, 1L);

        // then
        verify(rateRepository, times(1)).existsByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong());
        verify(rateRepository, times(1)).deleteByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("평점 삭제 실패 - 일치하는 평점 없음")
    void deleteRateFailed_RateNotFound() {

        // given
        given(rateRepository.existsByEpisode_EpisodeIdAndUser_UserId(anyLong(), anyLong())).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> rateService.deleteRate(1L, 1L));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.RATE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("웹툰 평점 불러오기 성공")
    void getWebtoonAvgSuccess() {

        // given
        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L).build();

        List<Episode> episodeList = Arrays.asList(
            Episode.builder()
                .episodeId(1L)
                .webtoon(webtoon)
                .build(),
            Episode.builder()
                .episodeId(2L)
                .webtoon(webtoon)
                .build()
        );

        List<User> userList = Arrays.asList(
            User.builder()
                .userId(1L)
                .build(),
            User.builder()
                .userId(2L)
                .build()
        );


        List<Rate> rateList = Arrays.asList(
            Rate.builder()
                .userRate(3)
                .episode(episodeList.get(0))
                .user(userList.get(0))
                .build(),
            Rate.builder()
                .userRate(2)
                .episode(episodeList.get(1))
                .user(userList.get(0))
                .build(),
            Rate.builder()
                .userRate(5)
                .episode(episodeList.get(0))
                .user(userList.get(1))
                .build()
        );

        int sum = 0;
        for (Rate rate : rateList) {
            sum += rate.getUserRate();
        }

        double avg = (double) sum / rateList.size();

        given(webtoonRepository.getAvgRate(anyLong())).willReturn(avg);

        // when
        RateAvgDto webtoonAvgRate = rateService.getWebtoonAvgRate(1L);

        // then
        assertEquals(avg, webtoonAvgRate.getAvgRate());
    }

    @Test
    @DisplayName("웹툰 에피소드 평점 불러오기 성공")
    void getEpisodeAvgSuccess() {

        // given
        Episode episode = Episode.builder()
            .episodeId(1L).build();

        List<User> userList = Arrays.asList(
            User.builder()
                .userId(1L)
                .build(),
            User.builder()
                .userId(2L)
                .build()
        );


        List<Rate> rateList = Arrays.asList(
            Rate.builder()
                .userRate(3)
                .episode(episode)
                .user(userList.get(0))
                .build(),
            Rate.builder()
                .userRate(2)
                .episode(episode)
                .user(userList.get(0))
                .build(),
            Rate.builder()
                .userRate(5)
                .episode(episode)
                .user(userList.get(1))
                .build()
        );

        int sum = 0;
        for (Rate rate : rateList) {
            sum += rate.getUserRate();
        }

        double avg = (double) sum / rateList.size();

        given(episodeRepository.getAvgRate(anyLong())).willReturn(avg);

        // when
        RateAvgDto episodeAvgRate = rateService.getWebtoonEpisodeAvgRate(1L);

        // then
        assertEquals(avg, episodeAvgRate.getAvgRate());
    }
}