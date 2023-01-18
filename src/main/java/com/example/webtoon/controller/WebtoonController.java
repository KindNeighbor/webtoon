package com.example.webtoon.controller;

import com.example.webtoon.dto.ApiResponse;
import com.example.webtoon.dto.EpisodeDto;
import com.example.webtoon.dto.WebtoonDto;
import com.example.webtoon.service.WebtoonService;
import com.example.webtoon.type.ResponseCode;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class WebtoonController {

    private final WebtoonService webtoonService;

    // 신규 웹툰 등록
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/webtoon")
    public ApiResponse<WebtoonDto> addWebtoon(@RequestParam String title,
                                     @RequestParam String artist,
                                     @RequestParam String day,
                                     @RequestParam String genre,
                                     @RequestParam MultipartFile file) throws IOException {

        WebtoonDto webtoonDto = webtoonService.addWebtoon(title, artist, day, genre, file);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.CREATE_NEW_WEBTOON, webtoonDto);
    }

    // 웹툰 수정
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/webtoon/{webtoonId}")
    public ApiResponse<WebtoonDto> updateWebtoon(@PathVariable Long webtoonId,
                                        @RequestParam String title, @RequestParam String artist,
                                        @RequestParam String day, @RequestParam String genre,
                                        @RequestParam MultipartFile file) throws IOException {

        WebtoonDto webtoonDto =
            webtoonService.updateWebtoon(webtoonId, title, artist, day, genre,file);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.UPDATE_WEBTOON_SUCCESS, webtoonDto);
    }

    // 기존 웹툰 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/webtoon/{webtoonId}")
    public ApiResponse<Void> deleteWebtoon(@PathVariable Long webtoonId) {
        webtoonService.deleteWebtoon(webtoonId);
        return new ApiResponse<>(HttpStatus.OK, ResponseCode.DELETE_WEBTOON_SUCCESS);
    }

    // 에피소드 등록
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/episode/{webtoonId}")
    public ApiResponse<EpisodeDto> addWebtoon(@PathVariable Long webtoonId,
                                     @RequestParam String title,
                                     @RequestParam MultipartFile epFile,
                                     @RequestParam MultipartFile thFile) throws IOException {

        EpisodeDto EpisodeDto =
            webtoonService.addEpisode(webtoonId, title, epFile, thFile);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.CREATE_NEW_EPISODE, EpisodeDto);
    }

    // 에피소드 수정
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/episode/{episodeId}")
    public ApiResponse<EpisodeDto> updateWebtoon(@PathVariable Long episodeId,
                                        @RequestParam String title,
                                        @RequestParam MultipartFile epFile,
                                        @RequestParam MultipartFile thFile) throws IOException {

        EpisodeDto EpisodeDto =
            webtoonService.updateEpisode(episodeId, title, epFile, thFile);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.UPDATE_EPISODE_SUCCESS, EpisodeDto);
    }

    // 기존 에피소드 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/episode/{episodeId}")
    public ApiResponse<Void> deleteEpisode(@PathVariable Long episodeId) {
        webtoonService.deleteEpisode(episodeId);
        return new ApiResponse<>(HttpStatus.OK, ResponseCode.DELETE_EPISODE_SUCCESS);
    }

    // 웹툰 에피소드 조회
    @GetMapping("/webtoon/episodes/{webtoonId}")
    public ApiResponse<Page<EpisodeDto>> getWebtoonEpisodes(@PathVariable Long webtoonId,
                                                            @RequestParam(defaultValue = "0") Integer page) {
        Page<EpisodeDto> episodeDtoList = webtoonService.getWebtoonEpisodes(webtoonId, page);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_EPISODES_SUCCESS, episodeDtoList);
    }

    // 웹툰 요일별 조회 (업데이트순, 평점순, 조회수순)
    @GetMapping("/webtoon")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<WebtoonDto>> getWebtoonByDay(
        @RequestParam(defaultValue = "월요일") String day,
        @RequestParam(defaultValue = "new") String orderType,
        @RequestParam(defaultValue = "0") Integer page) {

        Page<WebtoonDto> webtoonList = webtoonService.getWebtoonByDay(day, orderType, page);

        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_WEBTOON_BY_DAY_SUCCESS, webtoonList);
    }

    // 검색한 웹툰 조회
    @GetMapping("/webtoon-search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<WebtoonDto>> searchWebtoons(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "0") Integer page) {

        Page<WebtoonDto> webtoons = webtoonService.searchWebtoons(keyword, page);

        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_WEBTOON_BY_SEARCH, webtoons);
    }
}
