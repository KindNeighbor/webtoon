package com.example.webtoon.controller;


import com.example.webtoon.config.RestPage;
import com.example.webtoon.dto.ApiResponse;
import com.example.webtoon.dto.CommentDto;
import com.example.webtoon.dto.EpisodeIdListDto;
import com.example.webtoon.dto.UserInfo;
import com.example.webtoon.dto.WebtoonIdListDto;
import com.example.webtoon.config.CurrentUser;
import com.example.webtoon.security.UserPrincipal;
import com.example.webtoon.service.UserService;
import com.example.webtoon.type.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // 본인 정보 조회
    @GetMapping("/user/my")
    public ApiResponse<UserInfo> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserInfo userInfo = userService.getCurrentUser(currentUser);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_MY_INFO_SUCCESS, userInfo);
    }

    // 회원조회(관리자)
    @GetMapping("/admin/user/{nickname}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<UserInfo> getUserProfile(@PathVariable(value = "nickname") String nickname) {
        UserInfo userInfo = userService.getUserInfo(nickname);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_USER_INFO_SUCCESS, userInfo);
    }

    // 유저가 평점 부여한 에피소드 목록 조회
    @Cacheable(key = "#currentUser.id + ', givAvg' + ', page: ' + #page", value = "episodeAvgListbyUser")
    @GetMapping("/user/webtoon/rated")
    public ApiResponse<Page<EpisodeIdListDto>> getEpisodeRatedByUser(@CurrentUser UserPrincipal currentUser,
                                                                     @RequestParam(defaultValue = "0") Integer page) {
        Page<EpisodeIdListDto> episodeList =
            userService.getEpisodeRatedByUser(currentUser.getId(), page);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_RATED_EPISODE_LIST_SUCCESS, new RestPage<>(episodeList));
    }

    // 유저가 작성한 댓글 목록 조회
    @Cacheable(key = "#currentUser.id + ', comment' + ', page: ' + #page", value = "commentListbyUser")
    @GetMapping("/user/comments")
    public ApiResponse<Page<CommentDto>> getCommentsByUser(@CurrentUser UserPrincipal currentUser,
                                                           @RequestParam(defaultValue = "0") Integer page) {
        Page<CommentDto> commentList = userService.getCommentsByUser(currentUser.getId(), page);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_COMMENT_LIST_SUCCESS, new RestPage<>(commentList));
    }

    // 선호 작품 목록 조회
    @Cacheable(key = "#currentUser.id + ', Fav' + ', page: ' + #page", value = "FavList")
    @GetMapping("/user/fav-webtoon")
    public ApiResponse<Page<WebtoonIdListDto>> getFavWebtoonList(@CurrentUser UserPrincipal currentUser,
                                                                 @RequestParam(defaultValue = "0") Integer page) {
        Page<WebtoonIdListDto> favWebtoonList =
            userService.getFavWebtoonList(currentUser.getId(), page);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_FAV_WEBTOON_LIST_SUCCESS, new RestPage<>(favWebtoonList));
    }
}