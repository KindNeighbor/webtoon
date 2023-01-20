package com.example.webtoon.controller;

import com.example.webtoon.config.RestPage;
import com.example.webtoon.dto.ApiResponse;
import com.example.webtoon.dto.CommentDto;
import com.example.webtoon.security.CurrentUser;
import com.example.webtoon.security.UserPrincipal;
import com.example.webtoon.service.CommentService;
import com.example.webtoon.type.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 신규 작성
    @PostMapping("/comment/{episodeId}")
    public ApiResponse<CommentDto> createComment(@PathVariable Long episodeId,
                                                 @CurrentUser UserPrincipal currentUser,
                                                 @RequestBody String userComment) {
        CommentDto comment =
            commentService.createComment(episodeId, currentUser.getId(), userComment);

        return new ApiResponse<>(HttpStatus.OK, ResponseCode.CREATE_COMMENT_SUCCESS, comment);
    }

    // 댓글 전체 목록 조회
    @Cacheable(key = "#episodeId + ', page: ' + #page", value = "commentList")
    @GetMapping("/comment/{episodeId}")
    public ApiResponse<Page<CommentDto>> getCommentList(@PathVariable Long episodeId,
                                                        @RequestParam(defaultValue = "0") Integer page) {
        Page<CommentDto> commentList = commentService.getCommentList(episodeId, page);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.GET_COMMENT_LIST_SUCCESS, new RestPage<>(commentList));
    }

    // 댓글 수정
    @PutMapping("/comment/{commentId}")
    public ApiResponse<CommentDto> updateComment(@PathVariable Long commentId,
                                                 @CurrentUser UserPrincipal currentUser,
                                                 @RequestBody String userComment) {
        CommentDto commentDto =
            commentService.updateComment(commentId, currentUser.getId(), userComment);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.UPDATE_COMMENT_SUCCESS, commentDto);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId,
                                           @CurrentUser UserPrincipal currentUser) {
        commentService.deleteComment(commentId, currentUser.getId());
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.DELETE_COMMENT_SUCCESS);
    }

    // 댓글 삭제 (관리자만)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/admin/comment/{commentId}")
    public ApiResponse<Void> deleteCommentByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
        return new ApiResponse<>(
            HttpStatus.OK, ResponseCode.DELETE_COMMENT_SUCCESS);
    }
}
