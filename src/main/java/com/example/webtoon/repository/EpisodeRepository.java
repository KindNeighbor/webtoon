package com.example.webtoon.repository;

import com.example.webtoon.entity.Episode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    List<Episode> findByWebtoon_WebtoonId(Long webtoonId);
    Boolean existsByWebtoon_WebtoonIdAndTitle(Long webtoonId, String title);

    @Query(value="Select avg(user_rate) from example.rate "
        + "JOIN example.episode e on e.episode_id = rate.episode_id "
        + "where webtoon_id = ?1", nativeQuery = true)
    Double getAvgRate(Long id);
}
