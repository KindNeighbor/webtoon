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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final int SIZE = 10;

    private final EpisodeRepository episodeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 댓글 신규 작성
    @Caching(evict = {@CacheEvict(value = "commentList", allEntries = true),
                      @CacheEvict(value = "commentListbyUser", allEntries = true)})
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
    @Caching(evict = {@CacheEvict(value = "commentList", allEntries = true),
                      @CacheEvict(value = "commentListbyUser", allEntries = true)})
    public CommentDto updateComment(Long commentId, Long userId, String userComment) {
        Comment comment = commentRepository.findByCommentIdAndUser_UserId(commentId, userId)
            .orElseThrow(() -> new CustomException(
                HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND));

        comment.setComment(userComment);
        commentRepository.save(comment);

        return CommentDto.from(comment);
    }

    // 댓글 삭제
    @Caching(evict = {@CacheEvict(value = "commentList", allEntries = true),
                      @CacheEvict(value = "commentListbyUser", allEntries = true)})
    public void deleteComment(Long commentId, Long userId) {

        // 댓글이 있는지 확인
        if (!commentRepository.existsByCommentId(commentId)) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND);
        }

        // 그 댓글이 회원이 작성한 건지 확인
        if (!commentRepository.existsByUserUserId(userId)) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
        }

        commentRepository.deleteByCommentIdAndUser_UserId(commentId, userId);
    }

    // 댓글 삭제 (관리자)
    @Caching(evict = {@CacheEvict(value = "commentList", allEntries = true),
                      @CacheEvict(value = "commentListbyUser", allEntries = true)})
    public void deleteCommentByAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND);
        }
        commentRepository.deleteById(commentId);
    }

    // 에피소드 별 댓글 전체 목록 조회
    public Page<CommentDto> getCommentList(Long episodeId, Integer page) {
        Pageable pageable = PageRequest.of(page, SIZE);
        Page<Comment> commentList = commentRepository.findAllByEpisode_EpisodeId(episodeId, pageable);
        return commentList.map(CommentDto::from);
    }
}
