package com.example.webtoon.service;

import com.example.webtoon.dto.CommentDto;
import com.example.webtoon.entity.Comment;
import com.example.webtoon.entity.Episode;
import com.example.webtoon.entity.User;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.CommentRepository;
import com.example.webtoon.repository.EpisodeRepository;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.type.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final EpisodeRepository episodeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 댓글 신규 작성
    public CommentDto createComment(Long episodeId, Long userId, String userComment) {

        Episode episode = episodeRepository.findById(episodeId).orElseThrow(
            () -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.EPISODE_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(
            () -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        Comment comment = new Comment(userComment);
        comment.setEpisode(episode);
        comment.setUser(user);
        commentRepository.save(comment);

        return CommentDto.from(comment);
    }

    // 댓글 수정
    public CommentDto updateComment(Long commentId, Long userId, String userComment) {
        Comment comment = commentRepository.findByCommentIdAndUser_UserId(commentId, userId)
            .orElseThrow(() -> new CustomException(
                HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND));

        comment.setComment(userComment);
        commentRepository.save(comment);

        return CommentDto.from(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, Long userId) {
        if (!commentRepository.existsByCommentIdAndUser_UserId(commentId, userId)) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND);
        }
        commentRepository.deleteByCommentIdAndUser_UserId(commentId, userId);
    }

    // 댓글 삭제 (관리자)
    public void deleteCommentByAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND);
        }
        commentRepository.deleteById(commentId);
    }

    // 댓글 전체 목록 조회
    public List<CommentDto> getCommentList(Long episodeId) {
        List<Comment> commentList = commentRepository.findAllByEpisodeId(episodeId);
        return commentList.stream().map(CommentDto::from).collect(Collectors.toList());
    }
}
