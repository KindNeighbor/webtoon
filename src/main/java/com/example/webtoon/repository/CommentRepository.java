package com.example.webtoon.repository;

import com.example.webtoon.entity.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByEpisode_EpisodeId(Long episodeId, Pageable pageable);

    Optional<Comment> findByCommentIdAndUser_UserId(Long commentId, Long userId);

    boolean existsByUserUserId(Long userId);

    boolean existsByCommentId(Long commentId);

    void deleteByCommentIdAndUser_UserId(Long episodeId, Long userId);

    Page<Comment> findAllByUser_UserId(Long userId, Pageable pageable);
}
