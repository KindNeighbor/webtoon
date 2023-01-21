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
import com.example.webtoon.dto.WebtoonDocument;
import com.example.webtoon.dto.WebtoonDto;
import com.example.webtoon.entity.Episode;
import com.example.webtoon.entity.EpisodeFile;
import com.example.webtoon.entity.EpisodeThumbnail;
import com.example.webtoon.entity.Webtoon;
import com.example.webtoon.entity.WebtoonThumbnail;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.EpisodeRepository;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.repository.WebtoonSearchRepository;
import com.example.webtoon.type.ErrorCode;
import com.example.webtoon.type.SortType;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    @Mock
    private WebtoonSearchRepository webtoonSearchRepository;
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
        verify(webtoonSearchRepository, times(1)).save(any());
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
        assertEquals(ErrorCode.ALREADY_EXIST_WEBTOON_TITLE, exception.getErrorCode());
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
            "testDay", "updateGenre", multipartFile);

        // then
        verify(webtoonRepository, times(1)).save(any());
        assertEquals("updateTitle", webtoon.getTitle());
        assertEquals("updateArtist", webtoon.getArtist());
        assertEquals("testDay", webtoon.getDay());
        assertEquals("updateGenre", webtoon.getGenre());
        assertEquals("testFileName2", webtoon.getWebtoonThumbnail().getFileName());
        assertEquals("testUri2", webtoon.getWebtoonThumbnail().getFileUri());
    }

    @Test
    @DisplayName("웹툰 수정 실패 - 일치하는 웹툰 없음")
    void updateWebtoonFailed_WebtoonNotFound() {

        // given
        MultipartFile multipartFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());

        given(webtoonRepository.findById(anyLong())).willReturn(Optional.empty());


        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.updateWebtoon(10L, "updateTitle", "updateArtist",
                "testDay", "updateGenre", multipartFile));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.WEBTOON_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("웹툰 삭제 성공")
    void deleteWebtoonSuccess() {

        // given
        given(webtoonRepository.existsById(anyLong())).willReturn(true);

        // when
        webtoonService.deleteWebtoon(1L);

        // then
        verify(webtoonRepository, times(1)).existsById(anyLong());
        verify(webtoonRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("웹툰 삭제 실패 - 일치하는 웹툰 없음")
    void deleteWebtoonFailed_WebtoonNotFound() {

        // given
        given(webtoonRepository.existsById(anyLong())).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.deleteWebtoon(1L));

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
    void createEpisodeFailed_AlreadyExistTitle() {

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
        assertEquals(ErrorCode.ALREADY_EXIST_EPISODE_TITLE, exception.getErrorCode());
    }

    @Test
    @DisplayName("에피소드 등록 실패 - 일치하는 웹툰 없음")
    void createEpisodeFailed_WebtoonNotFound() {

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
    void updateEpisodeFailed_EpisodeNotFound() {

        // given
        MultipartFile epFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());
        MultipartFile thFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "byte", "test.jpg".getBytes());

        given(episodeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.updateEpisode(10L, "testTitle", epFile, thFile));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("에피소드 삭제 성공")
    void deleteEpisodeSuccess() {

        // given
        given(episodeRepository.existsById(anyLong())).willReturn(true);

        // when
        webtoonService.deleteEpisode(1L);

        // then
        verify(episodeRepository, times(1)).existsById(anyLong());
        verify(episodeRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("에피소드 삭제 실패 - 일치하는 에피소드 없음")
    void deleteEpisodeFailed_EpisodeNotFound() {

        // given
        given(episodeRepository.existsById(anyLong())).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.deleteEpisode(1L));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("요일별 웹툰 불러오기 성공")
    void getWebtoonByDaySuccess() {

        // given
        int page = 0;
        String day = "월요일";
        SortType sortType = SortType.NEW;

        List<Webtoon> webtoonList = Arrays.asList(
            Webtoon.builder()
                .title("테스트1")
                .artist("테스트 작가1")
                .genre("테스트 장르1")
                .build(),
            Webtoon.builder()
                .title("테스트2")
                .artist("테스트 작가2")
                .genre("테스트 장르2")
                .build()
        );

        Page<Webtoon> webtoons = new PageImpl<>(webtoonList);

        given(webtoonRepository.existsByDayContaining(anyString())).willReturn(true);
        given(webtoonRepository.findByDay(anyString(), any())).willReturn(webtoons);

        // when
        Page<WebtoonDto> webtoonDtoPage = webtoonService.getWebtoonByDay(day, sortType, page);

        // then
        assertEquals("테스트1", webtoonDtoPage.getContent().get(0).getTitle());
        assertEquals("테스트2", webtoonDtoPage.getContent().get(1).getTitle());
        assertEquals("테스트 작가1", webtoonDtoPage.getContent().get(0).getArtist());
        assertEquals("테스트 작가2", webtoonDtoPage.getContent().get(1).getArtist());
        assertEquals("테스트 장르1", webtoonDtoPage.getContent().get(0).getGenre());
        assertEquals("테스트 장르2", webtoonDtoPage.getContent().get(1).getGenre());
        assertEquals(1, webtoonDtoPage.getTotalPages());
        assertEquals(2, webtoonDtoPage.getTotalElements());
    }

    @Test
    @DisplayName("요일별 웹툰 불러오기 실패 - 찾는 요일과 일치하는 웹툰 없음")
    void getWebtoonByDayFailed_WebtoonNotFoundByDay() {

        // given
        int page = 0;
        String day = "월요일";
        SortType sortType = SortType.NEW;

        given(webtoonRepository.existsByDayContaining(anyString())).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> webtoonService.getWebtoonByDay(day, sortType, page));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.WEBTOON_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("웹툰 에피소드 전체 목록 불러오기 성공")
    void getWebtoonEpisodesSuccess() {

        // given
        int page = 0;
        Webtoon webtoon = Webtoon.builder()
            .webtoonId(1L)
            .build();

        EpisodeFile episodeFile = new EpisodeFile("test", "test");
        EpisodeThumbnail episodeThumbnail = new EpisodeThumbnail("test", "test");


        List<Episode> episodeList = Arrays.asList(
            Episode.builder()
                .title("테스트1")
                .webtoon(webtoon)
                .episodeFile(episodeFile)
                .episodeThumbnail(episodeThumbnail)
                .build(),
            Episode.builder()
                .title("테스트2")
                .webtoon(webtoon)
                .episodeFile(episodeFile)
                .episodeThumbnail(episodeThumbnail)
                .build()
        );

        Page<Episode> episodes = new PageImpl<>(episodeList);

        given(episodeRepository.findByWebtoon_WebtoonId(anyLong(), any())).willReturn(episodes);

        // when
        Page<EpisodeDto> episodeDtoPage = webtoonService.getWebtoonEpisodes(webtoon.getWebtoonId(), page);
        System.out.println("episodeDtoPage = " + episodeDtoPage);

        // then
        assertEquals("테스트1", episodeDtoPage.getContent().get(0).getTitle());
        assertEquals("테스트2", episodeDtoPage.getContent().get(1).getTitle());
        assertEquals(1, episodeDtoPage.getTotalPages());
        assertEquals(2, episodeDtoPage.getTotalElements());
    }

    @Test
    @DisplayName("웹툰 검색 성공")
    void searchWebtoonSuccess() {

        // given
        int page = 0;
        String day = "월요일";
        SortType sortType = SortType.NEW;

        List<WebtoonDocument> webtoonDocumentList = Arrays.asList(
            WebtoonDocument.builder()
                .title("테스트1")
                .artist("테스트 작가1")
                .genre("테스트 장르1")
                .build(),
            WebtoonDocument.builder()
                .title("테스트2")
                .artist("테스트 작가2")
                .genre("테스트 장르2")
                .build()
        );

        Page<WebtoonDocument> webtoonDocuments = new PageImpl<>(webtoonDocumentList);

        given(webtoonSearchRepository.findByTitleOrArtistOrGenre(anyString(), anyString(), anyString(), any()))
            .willReturn(webtoonDocuments);

        // when
        Page<WebtoonDto> webtoonDtoPage = webtoonService.searchWebtoons("테스트", page);

        // then
        assertEquals("테스트1", webtoonDtoPage.getContent().get(0).getTitle());
        assertEquals("테스트2", webtoonDtoPage.getContent().get(1).getTitle());
        assertEquals("테스트 작가1", webtoonDtoPage.getContent().get(0).getArtist());
        assertEquals("테스트 작가2", webtoonDtoPage.getContent().get(1).getArtist());
        assertEquals("테스트 장르1", webtoonDtoPage.getContent().get(0).getGenre());
        assertEquals("테스트 장르2", webtoonDtoPage.getContent().get(1).getGenre());
        assertEquals(1, webtoonDtoPage.getTotalPages());
        assertEquals(2, webtoonDtoPage.getTotalElements());
    }
}