package com.example.webtoon.repository;

import com.example.webtoon.entity.Comment;
import com.example.webtoon.entity.Episode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEpisode_EpisodeId(Long episodeId);

    Optional<Comment> findByCommentIdAndUser_UserId(Long commentId, Long userId);

    boolean existsByCommentIdAndUser_UserId(Long episodeId, Long userId);

    void deleteByCommentIdAndUser_UserId(Long episodeId, Long userId);

    List<Comment> findAllByUser_UserId(Long userId);
}
