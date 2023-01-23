package com.example.webtoon.controller;

import com.example.webtoon.dto.ApiResponse;
import com.example.webtoon.dto.RateAvgDto;
import com.example.webtoon.dto.RateDto;
import com.example.webtoon.config.CurrentUser;
import com.example.webtoon.security.UserPrincipal;
import com.example.webtoon.service.RateService;
import com.example.webtoon.type.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Api(tags = {"평점 컨트롤러"})
public class RateController {

    private final RateService rateService;

    // 평점 등록
    @ApiOperation("평점 등록")
    @PostMapping("/rate/{episodeId}")
    public ApiResponse<RateDto> addRate(@PathVariable Long episodeId, @CurrentUser UserPrincipal currentUser,
                                  @RequestParam Integer userRate) {
        RateDto rateDto = rateService.addRate(episodeId, currentUser.getId(), userRate);
        return new ApiResponse<>(HttpStatus.OK, ResponseCode.CREATE_RATE_SUCCESS, rateDto);
    }

    // 평점 수정
    @ApiOperation("평점 수정")
    @PutMapping("/rate/{episodeId}")
    public ApiResponse<RateDto> updateRate(@PathVariable Long episodeId, @CurrentUser UserPrincipal currentUser,
                                     @RequestParam Integer userRate) {
        RateDto rateDto = rateService.updateRate(episodeId, currentUser.getId(), userRate);
        return new ApiResponse<>(HttpStatus.OK, ResponseCode.UPDATE_RATE_SUCCESS, rateDto);
    }

    // 평점 삭제
    @ApiOperation("평점 삭제")
    @DeleteMapping("/rate/{episodeId}")
    public ApiResponse<Void> deleteRate(@PathVariable Long episodeId,
        @CurrentUser UserPrincipal currentUser) {
        rateService.deleteRate(episodeId, currentUser.getId());
        return new ApiResponse<>(HttpStatus.OK, ResponseCode.DELETE_RATE_SUCCESS);
    }

    // 웹툰 평점 평균 불러오기
    @ApiOperation("웹툰 평점 평균 불러오기")
    @GetMapping("/webtoon-rate/{webtoonId}")
    public ApiResponse<RateAvgDto> getWebtoonAvgRate(@PathVariable Long webtoonId){
        RateAvgDto webtoonAvgRate = rateService.getWebtoonAvgRate(webtoonId);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_WEBTOON_AVG_RAGE_SUCCESS, webtoonAvgRate);
    }

    // 에피소드 평점 평균 불러오기
    @ApiOperation("에피소드 평점 평균 불러오기")
    @GetMapping("/episode-rate/{episodeId}")
    public ApiResponse<RateAvgDto> getAvgRate(@PathVariable Long episodeId){
        RateAvgDto webtoonEpisodeAvgRate = rateService.getWebtoonEpisodeAvgRate(episodeId);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_EPISODE_AVG_RAGE_SUCCESS, webtoonEpisodeAvgRate);
    }
}
