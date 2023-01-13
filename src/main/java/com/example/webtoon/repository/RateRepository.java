package com.example.webtoon.repository;

import com.example.webtoon.entity.Rate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByEpisode_EpisodeIdAndUser_UserId(Long episodeId, Long userId);

    Boolean existsByEpisode_EpisodeIdAndUser_UserId(Long episodeId, Long userId);

    void deleteByEpisode_EpisodeIdAndUser_UserId(Long episodeId, Long userId);
}
