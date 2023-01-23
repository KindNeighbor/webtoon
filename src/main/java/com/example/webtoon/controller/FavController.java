package com.example.webtoon.controller;

import com.example.webtoon.dto.ApiResponse;
import com.example.webtoon.dto.WebtoonIdListDto;
import com.example.webtoon.config.CurrentUser;
import com.example.webtoon.security.UserPrincipal;
import com.example.webtoon.service.FavService;
import com.example.webtoon.type.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Api(tags = {"선호작품 컨트롤러"})
public class FavController {

    private final FavService favService;

    // 선호 작품 등록
    @ApiOperation("선호 작품 등록")
    @PostMapping("/fav-webtoon/{webtoonId}")
    public ApiResponse<WebtoonIdListDto> addFavWebtoon(@PathVariable Long webtoonId,
                                                       @CurrentUser UserPrincipal currentUser) {
        WebtoonIdListDto favWebtoon = favService.addFavWebtoon(webtoonId, currentUser.getId());
        return new ApiResponse<>(HttpStatus.OK, ResponseCode.ADD_FAV_WEBTOON_SUCCESS, favWebtoon);
    }

    // 선호 작품 삭제
    @ApiOperation("선호 작품 삭제")
    @DeleteMapping("/fav-webtoon/{webtoonId}")
    public ApiResponse<Void> deleteFavWebtoon(@PathVariable Long webtoonId,
                                              @CurrentUser UserPrincipal currentUser) {
        favService.deleteFavWebtoon(webtoonId, currentUser.getId());
        return new ApiResponse<>(HttpStatus.OK, ResponseCode.DELETE_FAV_WEBTOON_SUCCESS);
    }
}
