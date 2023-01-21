package com.example.webtoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.example.webtoon.entity.Webtoon;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.ViewRepository;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.type.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ViewServiceTest {

    @Mock
    private WebtoonRepository webtoonRepository;
    @Mock
    private ViewRepository viewRepository;
    @InjectMocks
    private ViewService viewService;

    @Test
    @DisplayName("조회수 카운팅 성공 - 중복이 아닌 경우")
    void checkViewCountSuccess_NotConflict() {

        // given
        MockHttpServletRequest req = new MockHttpServletRequest();

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .viewCount(0L)
            .build();

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.of(webtoon));
        given(viewRepository.existsByUserIPAndWebtoon_WebtoonId(anyString(), anyLong())).willReturn(false);

        // when
        viewService.checkViewCount(webtoon.getWebtoonId(), req);

        // then
        verify(viewRepository, times(1)).save(any());
        verify(webtoonRepository, times(1)).save(any());
        assertEquals(1L, webtoon.getViewCount());
    }

    @Test
    @DisplayName("조회수 카운팅 성공 - 중복인 경우")
    void checkViewCountSuccess_Conflict() {

        // given
        MockHttpServletRequest req = new MockHttpServletRequest();

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .viewCount(0L)
            .build();

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.of(webtoon));
        given(viewRepository.existsByUserIPAndWebtoon_WebtoonId(anyString(), anyLong())).willReturn(true);

        // when
        viewService.checkViewCount(webtoon.getWebtoonId(), req);

        // then
        verify(viewRepository, times(0)).save(any());
        verify(webtoonRepository, times(0)).save(any());
        assertEquals(0L, webtoon.getViewCount());
    }

    @Test
    @DisplayName("조회수 카운팅 실패 - 일치하는 웹툰 없음")
    void checkViewCountFailed_WebtoonNotFound() {

        // given
        MockHttpServletRequest req = new MockHttpServletRequest();

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .viewCount(0L)
            .build();

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> viewService.checkViewCount(webtoon.getWebtoonId(), req));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.WEBTOON_NOT_FOUND, exception.getErrorCode());
    }
}