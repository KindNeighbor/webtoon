package com.example.webtoon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.example.webtoon.dto.CommentDto;
import com.example.webtoon.entity.Comment;
import com.example.webtoon.entity.Episode;
import com.example.webtoon.entity.User;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.CommentRepository;
import com.example.webtoon.repository.EpisodeRepository;
import com.example.webtoon.repository.UserRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EpisodeRepository episodeRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("신규 댓글 작성 성공")
    void createCommentSuccess() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        Comment comment = Comment.builder()
            .comment("테스트 댓글")
            .user(user)
            .episode(episode)
            .build();

        given(episodeRepository.findById(anyLong())).willReturn(Optional.of(episode));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        CommentDto commentDto = commentService.createComment(episode.getEpisodeId(), user.getUserId(),
            "테스트 댓글");

        // then
        verify(commentRepository, times(1)).save(any());
        assertEquals(comment.getComment(), commentDto.getUserComment());
    }

    @Test
    @DisplayName("신규 댓글 작성 실패 - 일치하는 에피소드 없음")
    void createCommentFailed_EpisodeNotFound() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        given(episodeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> commentService.createComment(episode.getEpisodeId(), user.getUserId(), "테스트 댓글"));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("신규 댓글 작성 실패 - 일치하는 회원 없음")
    void createCommentFailed_UserNotFound() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        given(episodeRepository.findById(anyLong())).willReturn(Optional.of(episode));
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> commentService.createComment(episode.getEpisodeId(), user.getUserId(), "테스트 댓글"));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateCommentSuccess() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        Comment comment = Comment.builder()
            .commentId(1L)
            .comment("테스트 댓글")
            .user(user)
            .episode(episode)
            .build();

        given(commentRepository.findByCommentIdAndUser_UserId(anyLong(), anyLong())).willReturn(Optional.of(comment));

        // when
        CommentDto commentDto = commentService.updateComment(comment.getCommentId(), user.getUserId(),
            "수정 테스트 댓글");

        // then
        verify(commentRepository, times(1)).save(any());
        assertEquals("수정 테스트 댓글", commentDto.getUserComment());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 일치하는 댓글 없음")
    void updateCommentFailed_CommentNotFound() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        given(commentRepository.findByCommentIdAndUser_UserId(anyLong(), anyLong()))
            .willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> commentService.updateComment(
            episode.getEpisodeId(), user.getUserId(), "수정 테스트 댓글"));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteCommentSuccess() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        Comment comment = Comment.builder()
            .commentId(1L)
            .comment("테스트 댓글")
            .user(user)
            .episode(episode)
            .build();

        given(commentRepository.existsByCommentId(anyLong())).willReturn(true);
        given(commentRepository.existsByUserUserId(anyLong())).willReturn(true);

        // when
        commentService.deleteComment(comment.getCommentId(), user.getUserId());

        // then
        verify(commentRepository, times(1)).existsByCommentId(anyLong());
        verify(commentRepository, times(1)).existsByUserUserId(anyLong());
        verify(commentRepository, times(1))
            .deleteByCommentIdAndUser_UserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 일치하는 댓글 없음")
    void deleteCommentFailed_CommentNotFound() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        given(commentRepository.existsByCommentId(anyLong())).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> commentService.deleteComment(
            episode.getEpisodeId(), user.getUserId()));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 일치하는 댓글 없음")
    void deleteCommentFailed_UserNotFound() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        given(commentRepository.existsByCommentId(anyLong())).willReturn(true);
        given(commentRepository.existsByUserUserId(anyLong())).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> commentService.deleteComment(
            episode.getEpisodeId(), user.getUserId()));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제(관리자) 성공")
    void deleteCommentByAdminSuccess() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        Comment comment = Comment.builder()
            .commentId(1L)
            .comment("테스트 댓글")
            .user(user)
            .episode(episode)
            .build();

        given(commentRepository.existsById(anyLong())).willReturn(true);

        // when
        commentService.deleteCommentByAdmin(comment.getCommentId());

        // then
        verify(commentRepository, times(1)).existsById(anyLong());
        verify(commentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("댓글 삭제(관리자) 실패 - 일치하는 댓글 없음")
    void deleteCommentByAdminFailed_CommentNotFound() {

        // given
        User user = User.builder()
            .userId(1L)
            .build();

        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        Comment comment = Comment.builder()
            .commentId(1L)
            .comment("테스트 댓글")
            .user(user)
            .episode(episode)
            .build();

        given(commentRepository.existsById(anyLong())).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> commentService.deleteCommentByAdmin(comment.getCommentId()));

        // then
        assertEquals(NOT_FOUND, exception.getStatusMessage());
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getCommentList() {

        // given
        int page = 0;
        Episode episode = Episode.builder()
            .episodeId(1L)
            .build();

        List<Comment> commentList = Arrays.asList(
            Comment.builder()
                .comment("테스트 댓글1")
                .build(),
            Comment.builder()
                .comment("테스트 댓글2")
                .build(),
            Comment.builder()
                .comment("테스트 댓글3")
                .build()
        );

        Page<Comment> comments = new PageImpl<>(commentList);

        given(commentRepository.findAllByEpisode_EpisodeId(anyLong(), any())).willReturn(comments);

        // when
        Page<CommentDto> commentDtoPage = commentService.getCommentList(episode.getEpisodeId(), page);

        // then
        assertEquals("테스트 댓글1", commentDtoPage.getContent().get(0).getUserComment());
        assertEquals("테스트 댓글2", commentDtoPage.getContent().get(1).getUserComment());
        assertEquals("테스트 댓글3", commentDtoPage.getContent().get(2).getUserComment());
    }
}