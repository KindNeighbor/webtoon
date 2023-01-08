package com.example.webtoon.repository;

import com.example.webtoon.entity.Rate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByEpisode_EpisodeIdAndUser_UserId(Long episodeId, Long userId);

    Boolean existsByEpisode_EpisodeIdAndUser_UserId(Long episodeId, Long userId);

    @Query(value="Select avg(user_rate) from example.rate where episode_id = ?1", nativeQuery = true)
    Double getAvgRate(Long id);
}
