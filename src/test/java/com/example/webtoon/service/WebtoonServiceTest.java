package com.example.webtoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.example.webtoon.dto.EpisodeDto;
import com.example.webtoon.dto.WebtoonDto;
import com.example.webtoon.entity.Episode;
import com.example.webtoon.entity.EpisodeFile;
import com.example.webtoon.entity.EpisodeThumbnail;
import com.example.webtoon.entity.Webtoon;
import com.example.webtoon.entity.WebtoonThumbnail;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.EpisodeRepository;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.type.ErrorCode;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class WebtoonServiceTest {

    @Mock
    private WebtoonRepository webtoonRepository;
    @Mock
    private EpisodeRepository episodeRepository;
    @Mock
    private FileService fileService;
    @InjectMocks
    private WebtoonService webtoonService;


    @Test
    @DisplayName("웹툰 등록 성공")
    void createWebtoonSuccess() throws IOException {

        // given
        MultipartFile multipartFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());

        WebtoonThumbnail webtoonThumbnail = fileService.saveWebtoonThumbnailFile(multipartFile);

        Webtoon webtoon = Webtoon.builder()
            .title("testTitle")
            .artist("testArtist")
            .day("testDay")
            .genre("testGenre")
            .webtoonThumbnail(webtoonThumbnail)
            .episodes(null)
            .build();

        WebtoonDto webtoonDto1 = WebtoonDto.from(webtoon);

        given(webtoonRepository.existsByTitle(anyString())).willReturn(false);

        // when
        WebtoonDto webtoonDto2 = webtoonService.addWebtoon(webtoon.getTitle(), webtoon.getArtist(),
            webtoon.getDay(), webtoon.getGenre(), multipartFile);

        // then
        verify(webtoonRepository, times(1)).save(any());
        assertEquals(webtoonDto1.getWebtoonId(), webtoonDto2.getWebtoonId());
        assertEquals(webtoonDto1.getTitle(), webtoonDto2.getTitle());
        assertEquals(webtoonDto1.getArtist(), webtoonDto2.getArtist());
        assertEquals(webtoonDto1.getDay(), webtoonDto2.getDay());
        assertEquals(webtoonDto1.getGenre(), webtoonDto2.getGenre());
    }

    @Test
    @DisplayName("웹툰 등록 실패 - 중복된 웹툰 제목")
    void createWebtoonFailed_AlreadyExistTitle() throws IOException {

        // given
        MultipartFile multipartFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());

        WebtoonThumbnail webtoonThumbnail = fileService.saveWebtoonThumbnailFile(multipartFile);

        Webtoon webtoon = Webtoon.builder()
            .title("testTitle")
            .artist("testArtist")
            .day("testDay")
            .genre("testGenre")
            .webtoonThumbnail(webtoonThumbnail)
            .episodes(null)
            .build();

        given(webtoonRepository.existsByTitle(anyString())).willReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.addWebtoon(webtoon.getTitle(), webtoon.getArtist(),
                webtoon.getDay(), webtoon.getGenre(), multipartFile));

        // then
        assertEquals(CONFLICT, exception.getStatusMessage());
        assertEquals(ErrorCode.ALREADY_EXISTED_WEBTOON_TITLE, exception.getErrorCode());
    }

    @Test
    @DisplayName("웹툰 수정 성공")
    void updateWebtoonSuccess() throws IOException {

        // given
        MultipartFile multipartFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());

        WebtoonThumbnail webtoonThumbnail1 =
            new WebtoonThumbnail("testFileName1", "testUri1");
        WebtoonThumbnail webtoonThumbnail2 =
            new WebtoonThumbnail("testFileName2", "testUri2");

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(10L)
            .title("testTitle")
            .artist("testArtist")
            .day("testDay")
            .genre("testGenre")
            .webtoonThumbnail(webtoonThumbnail1)
            .episodes(null)
            .build();

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.of(webtoon));
        given(fileService.saveWebtoonThumbnailFile(any())).willReturn(webtoonThumbnail2);


        // when
        webtoonService.updateWebtoon(10L, "updateTitle", "updateArtist",
            "updateDay", "updateGenre", multipartFile);

        // then
        verify(webtoonRepository, times(1)).save(any());
        assertEquals("updateTitle", webtoon.getTitle());
        assertEquals("updateArtist", webtoon.getArtist());
        assertEquals("updateDay", webtoon.getDay());
        assertEquals("updateGenre", webtoon.getGenre());
        assertEquals("testFileName2", webtoon.getWebtoonThumbnail().getFileName());
        assertEquals("testUri2", webtoon.getWebtoonThumbnail().getFileUri());
    }

    @Test
    @DisplayName("웹툰 수정 실패 - 일치하는 웹툰 없음")
    void updateWebtoonFailed_WebtoonNotFound() throws IOException {

        // given
        MultipartFile multipartFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());

        WebtoonThumbnail webtoonThumbnail =
            new WebtoonThumbnail("testFileName1", "testUri1");

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(10L)
            .title("testTitle")
            .artist("testArtist")
            .day("testDay")
            .genre("testGenre")
            .webtoonThumbnail(webtoonThumbnail)
            .episodes(null)
            .build();

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.empty());


        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.updateWebtoon(10L, "updateTitle", "updateArtist",
                "updateDay", "updateGenre", multipartFile));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.WEBTOON_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("에피소드 등록 성공")
    void createEpisodeSuccess() throws IOException {

        // given
        MultipartFile epFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());
        MultipartFile thFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(10L)
            .title("testTitle")
            .artist("testArtist")
            .day("testDay")
            .genre("testGenre")
            .webtoonThumbnail(null)
            .episodes(null)
            .build();

        EpisodeFile episodeFile = new EpisodeFile("test", "test");
        EpisodeThumbnail episodeThumbnail = new EpisodeThumbnail("test", "test");

        Episode episode = Episode.builder()
            .episodeId(10L)
            .title("testTitle")
            .episodeFile(episodeFile)
            .episodeThumbnail(episodeThumbnail)
            .build();

        EpisodeDto episodeDto1 = EpisodeDto.from(episode);

        given(episodeRepository.existsByWebtoon_WebtoonIdAndTitle(anyLong(), anyString())).willReturn(false);
        given(webtoonRepository.findById(anyLong())).willReturn(Optional.of(webtoon));
        given(fileService.saveEpisodeFile(any())).willReturn(episodeFile);
        given(fileService.saveEpisodeThumbnailFile(any())).willReturn(episodeThumbnail);

        // when
        EpisodeDto episodeDto2 = webtoonService.addEpisode(
            webtoon.getWebtoonId(), episode.getTitle(), epFile, thFile);

        // then
        verify(episodeRepository, times(1)).save(any());
        assertEquals(episodeDto1.getTitle(), episodeDto2.getTitle());

    }

    @Test
    @DisplayName("에피소드 등록 실패 - 에피소드 제목 중복")
    void createEpisodeFailed_AlreadyExistTitle() throws IOException {

        // given
        MultipartFile epFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());
        MultipartFile thFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());


        given(episodeRepository.existsByWebtoon_WebtoonIdAndTitle(anyLong(), anyString()))
            .willReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.addEpisode(10L, "testTitle", epFile, thFile));

        // then
        assertEquals(CONFLICT, exception.getStatusMessage());
        assertEquals(ErrorCode.ALREADY_EXISTED_EPISODE_TITLE, exception.getErrorCode());
    }

    @Test
    @DisplayName("에피소드 등록 실패 - 일치하는 웹툰 없음")
    void createEpisodeFailed_WebtoonNotFound() throws IOException {

        // given
        MultipartFile epFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());
        MultipartFile thFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());


        given(episodeRepository.existsByWebtoon_WebtoonIdAndTitle(anyLong(), anyString()))
            .willReturn(false);
        given(webtoonRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.addEpisode(10L, "testTitle", epFile, thFile));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.WEBTOON_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("에피소드 수정 성공")
    void updateEpisodeSuccess() throws IOException {

        // given
        MultipartFile epFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());
        MultipartFile thFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(10L)
            .title("testTitle")
            .artist("testArtist")
            .day("testDay")
            .genre("testGenre")
            .webtoonThumbnail(null)
            .episodes(null)
            .build();

        EpisodeFile episodeFile = new EpisodeFile("test", "test");
        EpisodeThumbnail episodeThumbnail = new EpisodeThumbnail("test", "test");

        Episode episode = Episode.builder()
            .episodeId(10L)
            .title("testTitle")
            .episodeFile(episodeFile)
            .episodeThumbnail(episodeThumbnail)
            .build();


        given(episodeRepository.findById(anyLong())).willReturn(Optional.of(episode));
        given(fileService.saveEpisodeFile(any())).willReturn(episodeFile);
        given(fileService.saveEpisodeThumbnailFile(any())).willReturn(episodeThumbnail);

        // when
        EpisodeDto episodeDto = webtoonService.updateEpisode(
            webtoon.getWebtoonId(), "updateTitle", epFile, thFile);

        // then
        verify(episodeRepository, times(1)).save(any());
        assertEquals("updateTitle", episodeDto.getTitle());
    }

    @Test
    @DisplayName("에피소드 수정 실패 - 일치하는 에피소드 없음")
    void updateEpisodeFailed_EpisodeNotFound() throws IOException {

        // given
        MultipartFile epFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());
        MultipartFile thFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());

        Webtoon webtoon = Webtoon.builder()
            .webtoonId(10L)
            .title("testTitle")
            .artist("testArtist")
            .day("testDay")
            .genre("testGenre")
            .webtoonThumbnail(null)
            .episodes(null)
            .build();

        given(episodeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.updateEpisode(10L, "testTitle", epFile, thFile));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, exception.getErrorCode());
    }
}