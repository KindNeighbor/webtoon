package com.example.webtoon.controller;

import com.example.webtoon.dto.ApiResponse;
import com.example.webtoon.dto.EpisodeDto;
import com.example.webtoon.dto.WebtoonDto;
import com.example.webtoon.service.WebtoonService;
import com.example.webtoon.type.ResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class WebtoonController {

    private final WebtoonService webtoonService;

    // 신규 웹툰 등록
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/add-webtoon")
    public ApiResponse<WebtoonDto> addWebtoon(@RequestParam String title,
                                     @RequestParam String artist,
                                     @RequestParam String day,
                                     @RequestParam String genre,
                                     @RequestParam MultipartFile file) {

        WebtoonDto webtoonDto = webtoonService.addWebtoon(title, artist, day, genre, file);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.CREATE_NEW_WEBTOON, webtoonDto);
    }

    // 웹툰 수정
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/update-webtoon/{webtoonId}")
    public ApiResponse<WebtoonDto> updateWebtoon(@PathVariable Long webtoonId,
                                        @RequestParam String title, @RequestParam String artist,
                                        @RequestParam String day, @RequestParam String genre,
                                        @RequestParam MultipartFile file) {

        WebtoonDto webtoonDto =
            webtoonService.updateWebtoon(webtoonId, title, artist, day, genre,file);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.UPDATE_WEBTOON_SUCCESS, webtoonDto);
    }

    // 기존 웹툰 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-webtoon/{webtoonId}")
    public ApiResponse<Void> deleteWebtoon(@PathVariable Long webtoonId) {
        webtoonService.deleteWebtoon(webtoonId);
        return new ApiResponse<>(HttpStatus.OK, ResponseCode.DELETE_WEBTOON_SUCCESS);
    }

    // 에피소드 등록
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/add-episode/{webtoonId}")
    public ApiResponse<EpisodeDto> addWebtoon(@PathVariable Long webtoonId,
                                     @RequestParam String title,
                                     @RequestParam MultipartFile epFile,
                                     @RequestParam MultipartFile thFile) {

        EpisodeDto EpisodeDto =
            webtoonService.addEpisode(webtoonId, title, epFile, thFile);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.CREATE_NEW_EPISODE, EpisodeDto);
    }

    // 에피소드 수정
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/update-episode/{episodeId}")
    public ApiResponse<EpisodeDto> updateWebtoon(@PathVariable Long episodeId,
                                        @RequestParam String title,
                                        @RequestParam MultipartFile epFile,
                                        @RequestParam MultipartFile thFile) {

        EpisodeDto EpisodeDto =
            webtoonService.updateEpisode(episodeId, title, epFile, thFile);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.UPDATE_EPISODE_SUCCESS, EpisodeDto);
    }

    // 기존 에피소드 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-episode/{episodeId}")
    public ApiResponse<Void> deleteEpisode(@PathVariable Long episodeId) {
        webtoonService.deleteEpisode(episodeId);
        return new ApiResponse<>(HttpStatus.OK, ResponseCode.DELETE_EPISODE_SUCCESS);
    }

    // 웹툰 에피소드 조회 (조회 부분은 추후 정렬하면서 구조 변경 예정)
    @GetMapping("/get-webtoon/episodes/{webtoonId}")
    public ApiResponse<List<EpisodeDto>> getWebtoonEpisodes(@PathVariable Long webtoonId) {
        List<EpisodeDto> episodeDtoList = webtoonService.getWebtoonEpisodes(webtoonId);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_EPISODES_SUCCESS, episodeDtoList);
    }

    // 요일로 웹툰 조회 (조회 부분은 추후 정렬하면서 구조 변경 예정)
    @GetMapping("/get-webtoon/day")
    public ApiResponse<List<WebtoonDto>> getWebtoonByDay(@RequestParam String day) {
        List<WebtoonDto> webtoonByDay = webtoonService.getWebtoonByDay(day);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_WEBTOON_BY_DAY_SUCCESS, webtoonByDay);
    }
}
